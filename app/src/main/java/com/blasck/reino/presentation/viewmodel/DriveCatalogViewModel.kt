package com.blasck.reino.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blasck.reino.data.importer.XlsxCharacterSheetImporter
import com.blasck.reino.domain.drive.DriveCharacterEntry
import com.blasck.reino.domain.drive.DriveRemoteFile
import com.blasck.reino.domain.importer.CharacterSheetImportResult
import com.blasck.reino.domain.model.CharacterImportMetadata
import com.blasck.reino.domain.repository.CharacterRepository
import com.blasck.reino.domain.repository.DriveCatalogRepository
import com.blasck.reino.domain.repository.DriveImageStorageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream

class DriveCatalogViewModel(
    private val driveRepository: DriveCatalogRepository,
    private val imageStorage: DriveImageStorageRepository,
    private val importer: XlsxCharacterSheetImporter,
    private val characterRepository: CharacterRepository,
    private val itemMapper: DriveCatalogItemMapper = DriveCatalogItemMapper(),
) : ViewModel() {
    private val _state = MutableStateFlow<DriveCatalogUiState>(DriveCatalogUiState.Loading)
    val state: StateFlow<DriveCatalogUiState> = _state.asStateFlow()

    fun load() {
        if (_state.value is DriveCatalogUiState.Ready) return
        refresh()
    }

    fun refresh() {
        _state.value = DriveCatalogUiState.Loading
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    val localCharacters = characterRepository.observeAll().first()
                    itemMapper.map(driveRepository.loadCatalog(), localCharacters)
                }
            }.onSuccess { items ->
                _state.value = DriveCatalogUiState.Ready(items = items)
            }.onFailure { error ->
                _state.value =
                    DriveCatalogUiState.Error(
                        error.message ?: "Nao foi possivel carregar os personagens do Drive.",
                    )
            }
        }
    }

    fun importCharacter(
        item: DriveCatalogItem,
        selectedImage: DriveRemoteFile? = item.entry.primaryImage,
    ) {
        val entry = item.entry
        val sheet = entry.primarySheet ?: return
        _state.value = DriveCatalogUiState.Importing(entry.displayName)
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    val bytes = driveRepository.downloadFile(sheet)
                    val result = ByteArrayInputStream(bytes).use(importer::import)
                    when (result) {
                        is CharacterSheetImportResult.Success -> {
                            val now = System.currentTimeMillis()
                            val localImage = selectedImage?.cacheImage().orEmpty()
                            characterRepository.saveImportedCharacter(
                                character =
                                    result.character.copy(
                                        image = localImage,
                                    ),
                                metadata =
                                    CharacterImportMetadata(
                                        sourceFileName = sheet.name,
                                        sheetFormat = result.format,
                                        importedAtEpochMillis = now,
                                        updatedAtEpochMillis = now,
                                        remoteSheetFileId = sheet.fileId,
                                        remoteImageFileId = selectedImage?.fileId.orEmpty(),
                                    ),
                            )
                        }

                        is CharacterSheetImportResult.Failure ->
                            error(result.reason.toDriveImportMessage())
                    }
                }
            }.onSuccess { characterId ->
                _state.value =
                    DriveCatalogUiState.Imported(
                        characterId = characterId,
                        name = entry.displayName,
                    )
            }.onFailure { error ->
                _state.value =
                    DriveCatalogUiState.Error(
                        error.message ?: "Nao foi possivel importar a ficha do Drive.",
                    )
            }
        }
    }

    fun updateCharacter(
        item: DriveCatalogItem,
        selectedImage: DriveRemoteFile? = item.entry.primaryImage,
    ) {
        val localId = item.localCharacterId ?: return
        val entry = item.entry
        val sheet = entry.primarySheet ?: return
        _state.value = DriveCatalogUiState.Importing(entry.displayName)
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    val current = characterRepository.findById(localId)
                        ?: error("Personagem local nao encontrado.")
                    val bytes = driveRepository.downloadFile(sheet)
                    val result = ByteArrayInputStream(bytes).use(importer::import)
                    when (result) {
                        is CharacterSheetImportResult.Success -> {
                            val localImage = selectedImage?.cacheImage().orEmpty()
                            characterRepository.updateImportedCharacter(
                                characterId = localId,
                                character =
                                    result.character.copy(
                                        image = localImage,
                                    ),
                                metadata =
                                    CharacterImportMetadata(
                                        sourceFileName = sheet.name,
                                        sheetFormat = result.format,
                                        importedAtEpochMillis = current.importMetadata.importedAtEpochMillis,
                                        updatedAtEpochMillis = System.currentTimeMillis(),
                                        remoteSheetFileId = sheet.fileId,
                                        remoteImageFileId = selectedImage?.fileId.orEmpty(),
                                    ),
                            )
                        }

                        is CharacterSheetImportResult.Failure ->
                            error(result.reason.toDriveImportMessage())
                    }
                    localId
                }
            }.onSuccess { characterId ->
                _state.value =
                    DriveCatalogUiState.Imported(
                        characterId = characterId,
                        name = entry.displayName,
                    )
            }.onFailure { error ->
                _state.value =
                    DriveCatalogUiState.Error(
                        error.message ?: "Nao foi possivel atualizar a ficha do Drive.",
                    )
            }
        }
    }

    private suspend fun DriveRemoteFile.cacheImage(): String {
        val bytes = driveRepository.downloadFile(this)
        return imageStorage.saveImage(this, bytes)
    }
}

data class DriveCatalogItem(
    val entry: DriveCharacterEntry,
    val localCharacterId: Long?,
    val versions: List<DriveCatalogSheetVersion>,
    val labels: List<String>,
    val state: DriveCatalogItemState,
)

data class DriveCatalogSheetVersion(
    val sheet: DriveRemoteFile,
    val localCharacterId: Long?,
    val state: DriveCatalogItemState,
)

enum class DriveCatalogItemState(val label: String) {
    UNAVAILABLE("Sem ficha"),
    NOT_IMPORTED("Nao importado"),
    IMPORTED("Importado"),
    UPDATED("Atualizado"),
    UPDATE_AVAILABLE("Atualizacao disponivel"),
}

sealed interface DriveCatalogUiState {
    data object Loading : DriveCatalogUiState

    data class Ready(
        val items: List<DriveCatalogItem>,
    ) : DriveCatalogUiState

    data class Importing(
        val name: String,
    ) : DriveCatalogUiState

    data class Imported(
        val characterId: Long,
        val name: String,
    ) : DriveCatalogUiState

    data class Error(
        val message: String,
    ) : DriveCatalogUiState
}
