package com.blasck.reino.presentation.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.blasck.reino.presentation.screen.model.CharacterModel
import com.blasck.reino.system.theme.KingdomTheme

class CharacterScreenController(original: CharacterModel) {
    val originalModel = original
}

@Composable
fun CharacterScreen(
    id: String,
    onEditing: ((Boolean)->Unit) -> Unit,
    onWaiting: () -> Unit
) {
    Text(text = "Teste")
}

@Preview(showBackground = true)
@Composable
fun CharacterScreenPreview() {
    KingdomTheme {
        CharacterScreen("",{}){}
    }
}