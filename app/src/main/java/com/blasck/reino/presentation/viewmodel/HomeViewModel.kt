package com.blasck.reino.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blasck.reino.domain.model.StoredCharacter
import com.blasck.reino.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: CharacterRepository,
) : ViewModel() {
    val characters: StateFlow<List<StoredCharacter>> =
        repository.observeAll().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    fun deleteCharacter(characterId: Long) {
        viewModelScope.launch {
            repository.delete(characterId)
        }
    }
}
