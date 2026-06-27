package com.blasck.reino.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blasck.reino.domain.repository.CharacterRepository
import com.blasck.reino.presentation.mapper.toPresentationModel
import com.blasck.reino.presentation.screen.model.CharacterModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CharacterViewModel(
    private val repository: CharacterRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<CharacterUiState>(CharacterUiState.Loading)
    val state: StateFlow<CharacterUiState> = _state.asStateFlow()

    private var observedId: Long? = null
    private var observation: Job? = null

    fun load(characterId: Long) {
        if (observedId == characterId && observation?.isActive == true) return
        observedId = characterId
        observation?.cancel()
        _state.value = CharacterUiState.Loading

        observation =
            viewModelScope.launch {
                repository.observeById(characterId).collect { stored ->
                    _state.value =
                        runCatching {
                            stored?.let {
                                CharacterUiState.Ready(
                                    character = it.toPresentationModel(),
                                    hasBackup = repository.hasBackup(it.id),
                                )
                            } ?: CharacterUiState.NotFound
                        }.getOrElse { error ->
                            CharacterUiState.Error(
                                error.message ?: "Erro desconhecido ao preparar a ficha.",
                            )
                        }
                }
            }
    }

    fun restoreLatestBackup(characterId: Long) {
        viewModelScope.launch {
            runCatching {
                repository.restoreLatestBackup(characterId)
            }.onFailure { error ->
                _state.value =
                    CharacterUiState.Error(
                        error.message ?: "NÃ£o foi possÃ­vel restaurar o backup.",
                    )
            }
        }
    }
}

sealed interface CharacterUiState {
    data object Loading : CharacterUiState

    data object NotFound : CharacterUiState

    data class Error(
        val message: String,
    ) : CharacterUiState

    data class Ready(
        val character: CharacterModel,
        val hasBackup: Boolean,
    ) : CharacterUiState
}
