package com.blasck.reino.presentation.layout

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
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
    LazyColumn(
        modifier = Modifier.padding(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(characters.list.size) { item ->
            CharacterCard(characters.list[item], onClick = {
                onCharacterClick(characters.list[item].id, characters.list[item].name)
            })
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