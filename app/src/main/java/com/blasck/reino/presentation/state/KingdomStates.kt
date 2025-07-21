package com.blasck.reino.presentation.state

import com.blasck.reino.presentation.models.response.CharacterList

sealed class CharacterListState{
    object Loading: CharacterListState()
    data class Success(val characterList: CharacterList): CharacterListState()
    data class Failure(val message: String): CharacterListState()
    data class Error(val throwable: Throwable?): CharacterListState()
}

sealed class CharacterScreenState{
    data class Success(val character: Character): CharacterScreenState()
    data class Failure(val message: String): CharacterScreenState()
    data class Error(val throwable: Throwable?): CharacterScreenState()
    object Editing: CharacterScreenState()
    object Loading: CharacterScreenState()
}