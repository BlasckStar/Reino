package com.blasck.reino.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blasck.reino.domain.model.StoredCharacter
import com.blasck.reino.presentation.viewmodel.CharacterSessionUiState
import com.blasck.reino.presentation.viewmodel.CharacterSessionViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CharacterSessionScreen(
    characterId: Long,
    viewModel: CharacterSessionViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(characterId) {
        viewModel.load(characterId)
    }

    when (val current = state) {
        CharacterSessionUiState.Loading ->
            CircularProgressIndicator(modifier = Modifier.padding(24.dp))

        is CharacterSessionUiState.Error ->
            Text(
                text = current.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(24.dp),
            )

        is CharacterSessionUiState.Ready ->
            SessionEditor(
                stored = current.character,
                saving = false,
                onSave = viewModel::save,
            )

        is CharacterSessionUiState.Saving ->
            SessionEditor(
                stored = current.character,
                saving = true,
                onSave = viewModel::save,
            )
    }
}

@Composable
private fun SessionEditor(
    stored: StoredCharacter,
    saving: Boolean,
    onSave: (String, String, String, String) -> Unit,
) {
    var hitPoints by remember(stored.id) {
        mutableStateOf(stored.session.currentHitPoints.toString())
    }
    var fatiguePoints by remember(stored.id) {
        mutableStateOf(stored.session.currentFatiguePoints.toString())
    }
    var manaPoints by remember(stored.id) {
        mutableStateOf(stored.session.currentManaPoints.toString())
    }
    var notes by remember(stored.id) { mutableStateOf(stored.session.notes) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stored.character.identity.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Text("Valores atuais da sessão. Os máximos continuam vindo da ficha XLSX.")

        SessionNumberField(
            label = "Vida",
            value = hitPoints,
            maximum = stored.character.attributes.hitPoints,
            onValueChange = { hitPoints = it },
        )
        SessionNumberField(
            label = "Fadiga",
            value = fatiguePoints,
            maximum = stored.character.attributes.fatiguePoints,
            onValueChange = { fatiguePoints = it },
        )
        SessionNumberField(
            label = "Mana",
            value = manaPoints,
            maximum = stored.character.attributes.manaPoints,
            onValueChange = { manaPoints = it },
        )
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Anotações da sessão") },
            minLines = 5,
            modifier = Modifier.fillMaxWidth(),
        )
        Button(
            onClick = { onSave(hitPoints, fatiguePoints, manaPoints, notes) },
            enabled = !saving,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (saving) {
                CircularProgressIndicator()
            } else {
                Text("Salvar sessão")
            }
        }
    }
}

@Composable
private fun SessionNumberField(
    label: String,
    value: String,
    maximum: Int,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.isEmpty() || newValue == "-" || newValue.toIntOrNull() != null) {
                onValueChange(newValue)
            }
        },
        label = { Text(label) },
        supportingText = { Text("Máximo da ficha: $maximum") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}
