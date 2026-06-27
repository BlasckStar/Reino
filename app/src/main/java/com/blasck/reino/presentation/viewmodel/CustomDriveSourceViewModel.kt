package com.blasck.reino.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blasck.reino.domain.drive.DriveFolderLinkParser
import com.blasck.reino.domain.drive.DriveSourceConfig
import com.blasck.reino.domain.drive.DriveSourceOption
import com.blasck.reino.domain.repository.DefaultDriveSourceProvider
import com.blasck.reino.domain.repository.DriveSourceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CustomDriveSourceViewModel(
    private val driveSourceRepository: DriveSourceRepository,
    private val defaultDriveSourceProvider: DefaultDriveSourceProvider,
) : ViewModel() {
    private val _state = MutableStateFlow(CustomDriveSourceUiState())
    val state: StateFlow<CustomDriveSourceUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            driveSourceRepository.observeSource().collect { source ->
                _state.update {
                    it.copy(
                        activeFolderId = source.rootFolderId,
                        isDefault = source.isDefault,
                    )
                }
            }
        }
        viewModelScope.launch {
            defaultDriveSourceProvider.observeSourceOptions().collect { options ->
                _state.update { it.copy(sourceOptions = options) }
            }
        }
    }

    fun saveOption(option: DriveSourceOption) {
        val link = option.link.trim()
        val folderId = DriveFolderLinkParser.extractFolderId(link)
        if (folderId == null) {
            _state.update {
                it.copy(
                    error = "A fonte ${option.name} nao tem um link valido de pasta do Drive.",
                    message = null,
                )
            }
            return
        }

        viewModelScope.launch {
            driveSourceRepository.saveCustomSource(
                DriveSourceConfig(
                    rootFolderId = folderId,
                    customLink = link,
                ),
            )
            _state.update {
                it.copy(
                    error = null,
                    message = "Fonte de importacao salva: ${option.name}.",
                )
            }
        }
    }

    fun clear() {
        viewModelScope.launch {
            driveSourceRepository.clearCustomSource()
            _state.update {
                it.copy(
                    error = null,
                    message = "Fonte padrao restaurada.",
                )
            }
        }
    }
}

data class CustomDriveSourceUiState(
    val activeFolderId: String = "",
    val isDefault: Boolean = true,
    val sourceOptions: List<DriveSourceOption> = emptyList(),
    val message: String? = null,
    val error: String? = null,
)
