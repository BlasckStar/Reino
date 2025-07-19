package com.blasck.reino.presentation.layout

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blasck.reino.framework.mock.CharacterListMock
import com.blasck.reino.presentation.components.CharacterCard
import com.blasck.reino.presentation.models.response.CharacterList

@Composable
fun CharacterListLayout(
    characters: CharacterList,
    onCharacterClick: (String, String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    val filteredList = remember(searchText, characters) {
        characters.list.filter {
            it.name.contains(searchText, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            placeholder = { Text("Buscar personagem...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            singleLine = true
        )

        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredList.size) { index ->
                val character = filteredList[index]
                CharacterCard(character, onClick = {
                    onCharacterClick(character.id, character.name)
                })
            }
        }
    }
}

@Preview
@Composable
fun CharacterListLayoutPreview() {
    CharacterListLayout(
        CharacterListMock.mockList
    ){ id, name -> }
}