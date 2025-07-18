package com.blasck.reino.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blasck.reino.framework.mock.SyrioAugustoModel
import com.blasck.reino.presentation.screen.model.CharacterModel
import com.blasck.reino.ui.theme.ReinoTheme
import com.blasck.reino.ui.theme.Typography

class CharacterScreenController(original: CharacterModel) {
    val originalModel = original
}

@Composable
fun CharacterScreen(modifier: Modifier = Modifier) {

    val controller by remember { mutableStateOf(CharacterScreenController(SyrioAugustoModel.model)) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = controller.originalModel.information.name,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
            style = Typography.titleLarge
        )
    }
    // informações do personagem


}

@Preview(showBackground = true)
@Composable
fun CharacterScreenPreview() {
    ReinoTheme {
        CharacterScreen()
    }
}