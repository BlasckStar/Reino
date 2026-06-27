package com.blasck.reino.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blasck.reino.domain.drive.DriveFolderLinkParser
import com.blasck.reino.domain.drive.DriveSourceConfig
import com.blasck.reino.domain.repository.DriveSourceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CustomDriveSourceViewModel(
    private val driveSourceRepository: DriveSourceRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(CustomDriveSourceUiState())
    val state: StateFlow<CustomDriveSourceUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            driveSourceRepository.observeSource().collect { source ->
                _state.update {
                    it.copy(
                        input = if (source.isDefault) it.input else source.customLink.ifBlank { source.rootFolderId },
                        activeFolderId = source.rootFolderId,
                        activeLink = source.customLink,
                        isDefault = source.isDefault,
                    )
                }
            }
        }
    }

    fun onInputChange(value: String) {
        _state.update {
            it.copy(
                input = value,
                error = null,
                message = null,
            )
        }
    }

    fun save() {
        val link = state.value.input.trim()
        val folderId = DriveFolderLinkParser.extractFolderId(link)
        if (folderId == null) {
            _state.update {
                it.copy(
                    error = "Cole um link valido de pasta do Drive ou o ID da pasta.",
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
                    message = "Fonte de importacao salva.",
                )
            }
        }
    }

    fun clear() {
        viewModelScope.launch {
            driveSourceRepository.clearCustomSource()
            _state.update {
                it.copy(
                    input = "",
                    error = null,
                    message = "Fonte padrao restaurada.",
                )
            }
        }
    }
}

data class CustomDriveSourceUiState(
    val input: String = "",
    val activeFolderId: String = "",
    val activeLink: String = "",
    val isDefault: Boolean = true,
    val message: String? = null,
    val error: String? = null,
)
