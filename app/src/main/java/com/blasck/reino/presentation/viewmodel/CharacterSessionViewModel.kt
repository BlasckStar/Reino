package com.blasck.reino.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blasck.reino.domain.model.CharacterSession
import com.blasck.reino.domain.model.StoredCharacter
import com.blasck.reino.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CharacterSessionViewModel(
    private val repository: CharacterRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<CharacterSessionUiState>(CharacterSessionUiState.Loading)
    val state: StateFlow<CharacterSessionUiState> = _state.asStateFlow()

    private var characterId: Long? = null

    fun load(id: Long) {
        if (characterId == id && _state.value !is CharacterSessionUiState.Error) return
        characterId = id

        viewModelScope.launch {
            repository.observeById(id).collect { stored ->
                _state.value =
                    stored?.let(CharacterSessionUiState::Ready)
                        ?: CharacterSessionUiState.Error("Ficha não encontrada.")
            }
        }
    }

    fun save(
        hitPoints: String,
        fatiguePoints: String,
        manaPoints: String,
        notes: String,
    ) {
        val ready = _state.value as? CharacterSessionUiState.Ready ?: return
        val hitPointsValue = hitPoints.toIntOrNull()
        val fatigueValue = fatiguePoints.toIntOrNull()
        val manaValue = manaPoints.toIntOrNull()

        if (hitPointsValue == null || fatigueValue == null || manaValue == null) {
            _state.value =
                CharacterSessionUiState.Error(
                    "Vida, fadiga e mana precisam ser números inteiros.",
                )
            return
        }

        _state.value = CharacterSessionUiState.Saving(ready.character)
        viewModelScope.launch {
            runCatching {
                repository.updateSession(
                    characterId = ready.character.id,
                    session =
                        CharacterSession(
                            currentHitPoints = hitPointsValue,
                            currentFatiguePoints = fatigueValue,
                            currentManaPoints = manaValue,
                            notes = notes,
                        ),
                )
            }.onFailure { error ->
                _state.value =
                    CharacterSessionUiState.Error(
                        "Não foi possível salvar a sessão. ${error.message.orEmpty()}".trim(),
                    )
            }
        }
    }
}

sealed interface CharacterSessionUiState {
    data object Loading : CharacterSessionUiState

    data class Ready(
        val character: StoredCharacter,
    ) : CharacterSessionUiState

    data class Saving(
        val character: StoredCharacter,
    ) : CharacterSessionUiState

    data class Error(
        val message: String,
    ) : CharacterSessionUiState
}
