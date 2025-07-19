package com.blasck.reino.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blasck.reino.domain.entity.KingdomResult
import com.blasck.reino.domain.usecase.GetCharacterListByTypeUseCase
import com.blasck.reino.framework.mock.CharacterListMock
import com.blasck.reino.presentation.models.response.CharacterList.Companion.fromEntity
import com.blasck.reino.presentation.state.CharacterListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ServiceViewModel(
    private val getCharacterListByType: GetCharacterListByTypeUseCase
): ViewModel() {

    private val _characterList: MutableStateFlow<CharacterListState> = MutableStateFlow(
        value =
        CharacterListState.Success(CharacterListMock.mockList)
        //CharacterListState.Failure(error = Exception("Error"))
//        CharacterListState.Error(throwable = Exception("Error"))
//        CharacterListState.Loading
    )
    val characterList = _characterList.asStateFlow()

    fun getCharacterList(filter: String){
        viewModelScope.launch {
            getCharacterListByType(filter).collect {
                when(it){
                    is KingdomResult.Error -> {
                        _characterList.emit(
                            CharacterListState.Error(it.throwable)
                        )
                    }
                    is KingdomResult.Failure -> {
                        _characterList.emit(
                            CharacterListState.Failure(it.error.message)
                        )
                    }
                    is KingdomResult.Success -> {
                        _characterList.emit(
                            CharacterListState.Success(it.data.fromEntity())
                        )
                    }
                }
            }
        }
    }

}