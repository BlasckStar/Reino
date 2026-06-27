package com.blasck.reino.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blasck.reino.data.importer.XlsxCharacterSheetImporter
import com.blasck.reino.domain.importer.CharacterSheetImportFailure
import com.blasck.reino.domain.importer.CharacterSheetImportResult
import com.blasck.reino.domain.model.Character
import com.blasck.reino.domain.model.CharacterImportMetadata
import com.blasck.reino.domain.model.CharacterSheetFormat
import com.blasck.reino.domain.model.StoredCharacter
import com.blasck.reino.domain.repository.CharacterRepository
import com.blasck.reino.domain.update.CharacterUpdatePreview
import com.blasck.reino.domain.update.previewUpdateWith
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

class CharacterImportViewModel(
    private val importer: XlsxCharacterSheetImporter,
    private val repository: CharacterRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<CharacterImportUiState>(CharacterImportUiState.Empty)
    val state: StateFlow<CharacterImportUiState> = _state.asStateFlow()

    private var pendingImport: PendingImport? = null

    fun import(
        input: InputStream,
        fileName: String,
        updateCharacterId: Long? = null,
    ) {
        _state.value = CharacterImportUiState.Loading
        viewModelScope.launch {
            val result =
                withContext(Dispatchers.IO) {
                    input.use(importer::import)
                }

            when (result) {
                is CharacterSheetImportResult.Success -> {
                    val pending =
                        PendingImport(
                            character = result.character,
                            fileName = fileName,
                            format = result.format,
                            updateCharacterId = updateCharacterId,
                        )
                    pendingImport = pending

                    if (updateCharacterId == null) {
                        _state.value = CharacterImportUiState.Preview(result.character)
                    } else {
                        val stored =
                            withContext(Dispatchers.IO) {
                                repository.findById(updateCharacterId)
                            }
                        if (stored == null) {
                            pendingImport = null
                            _state.value =
                                CharacterImportUiState.Error(
                                    "A ficha original nÃ£o foi encontrada para atualizaÃ§Ã£o.",
                                )
                        } else {
                            _state.value =
                                CharacterImportUiState.UpdateReview(
                                    current = stored,
                                    imported = result.character,
                                    preview = stored.character.previewUpdateWith(result.character),
                                )
                        }
                    }
                }

                is CharacterSheetImportResult.Failure -> {
                    pendingImport = null
                    _state.value = CharacterImportUiState.Error(result.reason.toUserMessage())
                }
            }
        }
    }

    fun confirm() {
        val pending = pendingImport ?: return
        _state.value = CharacterImportUiState.Saving

        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    val now = System.currentTimeMillis()
                    repository.saveImportedCharacter(
                        character = pending.character,
                        metadata =
                            CharacterImportMetadata(
                                sourceFileName = pending.fileName,
                                sheetFormat = pending.format,
                                importedAtEpochMillis = now,
                                updatedAtEpochMillis = now,
                            ),
                    )
                }
            }.onSuccess { id ->
                pendingImport = null
                _state.value = CharacterImportUiState.Saved(id)
            }.onFailure { error ->
                _state.value =
                    CharacterImportUiState.Error(
                        "Não foi possível salvar a ficha. ${error.message.orEmpty()}".trim(),
                    )
            }
        }
    }

    fun confirmUpdate() {
        val pending = pendingImport ?: return
        val characterId = pending.updateCharacterId ?: return
        _state.value = CharacterImportUiState.Saving

        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    val current = repository.findById(characterId)
                    if (current == null) {
                        false
                    } else {
                        repository.updateImportedCharacter(
                            characterId = characterId,
                            character = pending.character,
                            metadata =
                                CharacterImportMetadata(
                                    sourceFileName = pending.fileName,
                                    sheetFormat = pending.format,
                                    importedAtEpochMillis = current.importMetadata.importedAtEpochMillis,
                                    updatedAtEpochMillis = System.currentTimeMillis(),
                                ),
                        )
                    }
                }
            }.onSuccess { updated ->
                if (updated) {
                    pendingImport = null
                    _state.value = CharacterImportUiState.Saved(characterId)
                } else {
                    _state.value =
                        CharacterImportUiState.Error(
                            "NÃ£o foi possÃ­vel atualizar: a ficha original nÃ£o foi encontrada.",
                        )
                }
            }.onFailure { error ->
                _state.value =
                    CharacterImportUiState.Error(
                        "NÃ£o foi possÃ­vel atualizar a ficha. ${error.message.orEmpty()}".trim(),
                    )
            }
        }
    }

    private data class PendingImport(
        val character: Character,
        val fileName: String,
        val format: CharacterSheetFormat,
        val updateCharacterId: Long?,
    )
}

sealed interface CharacterImportUiState {
    data object Empty : CharacterImportUiState

    data object Loading : CharacterImportUiState

    data object Saving : CharacterImportUiState

    data class Preview(
        val character: Character,
    ) : CharacterImportUiState

    data class UpdateReview(
        val current: StoredCharacter,
        val imported: Character,
        val preview: CharacterUpdatePreview,
    ) : CharacterImportUiState

    data class Saved(
        val characterId: Long,
    ) : CharacterImportUiState

    data class Error(
        val message: String,
    ) : CharacterImportUiState
}

private fun CharacterSheetImportFailure.toUserMessage(): String =
    when (this) {
        CharacterSheetImportFailure.InvalidFile ->
            "O arquivo selecionado não é uma ficha XLSX válida."

        is CharacterSheetImportFailure.MissingSheet ->
            "A ficha não possui a aba obrigatória “$sheetName”."

        is CharacterSheetImportFailure.MissingRequiredField ->
            "O campo obrigatório “$fieldName” não foi encontrado na célula $cellAddress."

        is CharacterSheetImportFailure.InvalidFieldValue ->
            "O valor “$value” do campo “$fieldName” não é válido."

        is CharacterSheetImportFailure.ReadError ->
            "Não foi possível ler a ficha. ${message.orEmpty()}".trim()
    }
