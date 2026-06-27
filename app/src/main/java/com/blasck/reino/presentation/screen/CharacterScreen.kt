package com.blasck.reino.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.blasck.reino.presentation.components.KingdomLoading
import com.blasck.reino.presentation.layout.CharacterLayout
import com.blasck.reino.presentation.viewmodel.CharacterUiState
import com.blasck.reino.presentation.viewmodel.CharacterViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CharacterScreen(
    id: Long,
    onEditSession: () -> Unit,
    onSearch: () -> Unit,
    onUpdateSheet: () -> Unit,
    viewModel: CharacterViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(id) {
        viewModel.load(id)
    }

    when (val current = state) {
        CharacterUiState.Loading -> KingdomLoading()
        CharacterUiState.NotFound ->
            CharacterMessage(
                title = "Ficha não encontrada",
                message = "A ficha pode ter sido removida ou ainda não foi salva neste aparelho.",
            )

        is CharacterUiState.Error ->
            CharacterMessage(
                title = "Não foi possível abrir a ficha",
                message = current.message,
            )

        is CharacterUiState.Ready ->
            CharacterLayout(
                model = current.character,
                hasBackup = current.hasBackup,
                onEditSession = onEditSession,
                onSearch = onSearch,
                onUpdateSheet = onUpdateSheet,
                onRestoreBackup = { viewModel.restoreLatestBackup(id) },
            )
    }
}

@Composable
private fun CharacterMessage(
    title: String,
    message: String,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = message,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
