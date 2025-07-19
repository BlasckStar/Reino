package com.blasck.reino.presentation.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.blasck.reino.presentation.components.CoilImage
import com.blasck.reino.presentation.screen.model.CharacterModel

@Composable
fun CharacterLayout(
    model: CharacterModel
) {
    Column {
        CoilImage(model.image)
        Row {

        }
    }
}