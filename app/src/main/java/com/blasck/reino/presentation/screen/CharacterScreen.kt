package com.blasck.reino.presentation.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.blasck.reino.system.theme.KingdomTheme

@Composable
fun CharacterScreen(
    id: String,
    onEditing: (()-> Unit) -> Unit,
    toEditing: () -> Unit,
    onWaiting: () -> Unit
) {

    val editTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(
        Unit,
        editTrigger
    ) {
        onEditing(){
            toEditing()
        }
    }

    Text(text = "Character Screen")
}

@Preview(showBackground = true)
@Composable
fun CharacterScreenPreview() {
    KingdomTheme {
        CharacterScreen("",{},{}){}
    }
}