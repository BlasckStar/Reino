package com.blasck.reino.presentation.screen

import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.blasck.reino.domain.model.Character
import com.blasck.reino.domain.update.CharacterChange
import com.blasck.reino.domain.update.CharacterChangeCategory
import com.blasck.reino.domain.update.CharacterUpdatePreview
import com.blasck.reino.presentation.components.KingdomInlineLoading
import com.blasck.reino.presentation.utils.formatNumber
import com.blasck.reino.presentation.utils.formatWithMax
import com.blasck.reino.presentation.viewmodel.CharacterImportUiState
import com.blasck.reino.presentation.viewmodel.CharacterImportViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CharacterImportScreen(
    onSaved: (Long) -> Unit,
    onBack: () -> Unit,
    updateCharacterId: Long? = null,
    viewModel: CharacterImportViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        val saved = state as? CharacterImportUiState.Saved
        if (saved != null) onSaved(saved.characterId)
    }

    val documentPicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument(),
        ) { uri ->
            uri ?: return@rememberLauncherForActivityResult
            val input = context.contentResolver.openInputStream(uri)
            if (input != null) {
                val fileName =
                    context.contentResolver
                        .query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                        ?.use { cursor ->
                            if (cursor.moveToFirst()) cursor.getString(0) else null
                        }
                        ?: "ficha.xlsx"
                viewModel.import(input, fileName, updateCharacterId)
            }
        }

    CharacterImportContent(
        state = state,
        updateMode = updateCharacterId != null,
        onChooseFile = {
            documentPicker.launch(
                arrayOf(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                ),
            )
        },
        onConfirm = viewModel::confirm,
        onConfirmUpdate = viewModel::confirmUpdate,
        onBack = onBack,
    )
}

@Composable
private fun CharacterImportContent(
    state: CharacterImportUiState,
    updateMode: Boolean,
    onChooseFile: () -> Unit,
    onConfirm: () -> Unit,
    onConfirmUpdate: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = if (updateMode) "Atualizar pela ficha" else "Importar ficha",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            if (updateMode) {
                "Escolha a nova versao da ficha XLSX para revisar as alteracoes antes de aplicar."
            } else {
                "Escolha uma ficha do Reino no formato XLSX."
            },
        )

        when (state) {
            CharacterImportUiState.Empty -> Text("Nenhum arquivo selecionado.")
            CharacterImportUiState.Loading -> ProgressMessage("Lendo e validando a ficha…")
            CharacterImportUiState.Saving -> ProgressMessage("Salvando a ficha…")
            is CharacterImportUiState.Error -> ErrorCard(state.message)
            is CharacterImportUiState.Preview -> {
                CharacterPreview(state.character)
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Confirmar e salvar")
                }
            }
            is CharacterImportUiState.UpdateReview -> {
                CharacterUpdateReview(
                    preview = state.preview,
                    imported = state.imported,
                )
                Button(
                    onClick = onConfirmUpdate,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        if (state.preview.hasChanges) {
                            "Confirmar atualizacao"
                        } else {
                            "Confirmar ficha sem alteracoes"
                        },
                    )
                }
            }
            is CharacterImportUiState.Saved -> Text("Ficha salva com sucesso.")
        }

        Button(
            onClick = onChooseFile,
            enabled =
                state !is CharacterImportUiState.Loading &&
                    state !is CharacterImportUiState.Saving,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (updateMode) "Escolher nova ficha XLSX" else "Escolher arquivo XLSX")
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Voltar")
        }
    }
}

@Composable
private fun CharacterUpdateReview(
    preview: CharacterUpdatePreview,
    imported: Character,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        CharacterPreview(imported)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Revisao da atualizacao",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                PreviewGrid(columns = 3) {
                    PreviewStat("Adicionados", preview.addedCount.toString())
                    PreviewStat("Alterados", preview.changedCount.toString())
                    PreviewStat("Removidos", preview.removedCount.toString())
                }

                if (!preview.hasChanges) {
                    Text("Nenhuma diferenca encontrada entre a ficha atual e a nova ficha.")
                } else {
                    preview.changes
                        .groupBy(CharacterChange::category)
                        .forEach { (category, changes) ->
                            ChangeGroup(category, changes)
                        }
                }
            }
        }
    }
}

@Composable
private fun ChangeGroup(
    category: CharacterChangeCategory,
    changes: List<CharacterChange>,
) {
    PreviewSection("${category.label} (${changes.size})") {
        changes.forEach { change ->
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "${change.type.label}: ${change.label}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (change.currentValue.isNotBlank()) {
                        Text(
                            text = "Atual: ${change.currentValue}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (change.newValue.isNotBlank()) {
                        Text(
                            text = "Novo: ${change.newValue}",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressMessage(message: String) {
    KingdomInlineLoading(
        message = message,
        badgeSize = 72.dp,
    )
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
            ),
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onErrorContainer,
        )
    }
}

@Composable
private fun CharacterPreview(character: Character) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Prévia da ficha",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            PreviewSection(title = "Identidade") {
                PreviewValue("Nome", character.identity.name)
                PreviewValue("Jogador", character.identity.player)
                PreviewValue("Raça", character.identity.race)
                PreviewValue("Reino", character.identity.kingdom)
                PreviewValue("Idade", character.identity.age)
                PreviewValue("Pontos", character.identity.pointsToSpend.toString())
            }

            PreviewSection(title = "Atributos") {
                PreviewGrid(columns = 4) {
                    PreviewStat("ST", character.attributes.strength.toString())
                    PreviewStat("DX", character.attributes.dexterity.toString())
                    PreviewStat("IQ", character.attributes.intelligence.toString())
                    PreviewStat("HT", character.attributes.health.toString())
                }
            }

            PreviewSection(title = "Recursos") {
                PreviewGrid(columns = 3) {
                    PreviewStat(
                        label = "Vida",
                        value = character.attributes.hitPoints.formatWithMax(
                            character.attributes.maxHitPoints
                        )
                    )
                    PreviewStat(
                        label = "Fadiga",
                        value = character.attributes.fatiguePoints.formatWithMax(
                            character.attributes.maxFatiguePoints
                        )
                    )
                    PreviewStat(
                        label = "Mana",
                        value = character.attributes.manaPoints.formatWithMax(
                            character.attributes.maxManaPoints
                        )
                    )
                }
            }

            PreviewSection(title = "Secundários") {
                PreviewGrid(columns = 2) {
                    PreviewStat("Vontade", character.attributes.will.toString())
                    PreviewStat("Percepção", character.attributes.perception.toString())
                    PreviewStat("Velocidade", character.attributes.basicSpeed.formatNumber())
                    PreviewStat("Deslocamento", character.attributes.basicMove.toString())
                    PreviewStat("Carga básica", character.attributes.basicLift.formatNumber())
                    PreviewStat("Esquiva", character.attributes.dodge.toString())
                }
            }

            if (character.attributes.damageGDP.isNotBlank() || character.attributes.damageBAL.isNotBlank()) {
                PreviewSection(title = "Dano") {
                    PreviewGrid(columns = 2) {
                        PreviewStat("GDP", character.attributes.damageGDP)
                        PreviewStat("BAL", character.attributes.damageBAL)
                    }
                }
            }
        }
    }
}


@Composable
private fun PreviewValue(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            modifier = Modifier.width(96.dp),
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            modifier = Modifier.weight(1f),
            text = value.ifBlank { "-" },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun PreviewStat(
    label: String,
    value: String,
) {
    Surface(
        modifier = Modifier
            .widthIn(min = 72.dp)
            .heightIn(min = 64.dp),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )

            Text(
                text = value.ifBlank { "-" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun PreviewGrid(
    columns: Int,
    content: @Composable FlowRowScope.() -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = columns,
        content = content,
    )
}

@Composable
private fun PreviewSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            content()
        }
    }
}

//@Composable
//private fun PreviewValueLegacy(
//    label: String,
//    value: String,
//) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceBetween,
//    ) {
//        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
//        Text(value.ifBlank { "—" }, fontWeight = FontWeight.SemiBold)
//    }
//}
