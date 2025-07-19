package com.blasck.reino.presentation.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.blasck.reino.presentation.components.KingdomLoading
import com.blasck.reino.presentation.enums.CharacterListFilters
import com.blasck.reino.presentation.layout.CharacterListLayout
import com.blasck.reino.presentation.state.CharacterListState
import com.blasck.reino.presentation.viewmodel.ServiceViewModel

@Composable
fun CharacterListScreen(
    filter: CharacterListFilters,
    services: ServiceViewModel,
    goToCharacter: (String, String) -> Unit,
    showError: (String) -> Unit = {},
) {

    val characterListState = services.characterList.collectAsState()

    fun getCharacters(filter: CharacterListFilters) {
        //services.getCharacterList(filter.value)
    }

    LaunchedEffect(Unit) {
        getCharacters(filter)
    }

    Column {
        characterListState.value.let {
            when (it) {
                is CharacterListState.Loading -> {
                    KingdomLoading()
                }
                is CharacterListState.Success -> {
                    CharacterListLayout(it.characterList){ id, name ->
                        goToCharacter(id, name)
                    }
                }
                is CharacterListState.Failure -> { showError(it.message) }
                is CharacterListState.Error -> { showError(it.throwable?.message.toString()) }

            }
        }
    }
}