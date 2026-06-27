package com.blasck.reino.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.blasck.reino.R
import com.blasck.reino.domain.model.StoredCharacter
import com.blasck.reino.presentation.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import java.io.File

@Composable
fun LocalCharacterListScreen(
    onImportCharacter: () -> Unit,
    onOpenCharacter: (Long, String) -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val characters by viewModel.characters.collectAsState()
    CharacterListContent(
        characters = characters,
        onImportCharacter = onImportCharacter,
        onOpenCharacter = onOpenCharacter,
        onDeleteCharacter = viewModel::deleteCharacter,
    )
}

@Composable
private fun CharacterListContent(
    characters: List<StoredCharacter>,
    onImportCharacter: () -> Unit,
    onOpenCharacter: (Long, String) -> Unit,
    onDeleteCharacter: (Long) -> Unit,
) {
    var pendingDeletion by remember { mutableStateOf<StoredCharacter?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Lista de personagens",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Button(
                    onClick = onImportCharacter,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Importar personagem")
                }
            }
        }

        if (characters.isEmpty()) {
            item {
                EmptyCharacterListCard(onImportCharacter = onImportCharacter)
            }
        } else {
            items(characters, key = { it.id }) { stored ->
                CharacterListCard(
                    stored = stored,
                    onOpen = {
                        onOpenCharacter(stored.id, stored.character.identity.name)
                    },
                    onDelete = {
                        pendingDeletion = stored
                    },
                )
            }
        }
    }

    pendingDeletion?.let { stored ->
        AlertDialog(
            onDismissRequest = { pendingDeletion = null },
            title = { Text("Excluir ficha?") },
            text = {
                Text(
                    "A ficha de ${stored.character.identity.name} e os dados de sessao " +
                        "serao removidos deste aparelho.",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteCharacter(stored.id)
                        pendingDeletion = null
                    },
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeletion = null }) {
                    Text("Cancelar")
                }
            },
        )
    }
}

@Composable
private fun CharacterListCard(
    stored: StoredCharacter,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
) {
    val character = stored.character
    val identity = character.identity
    val subtitle =
        listOf(
            identity.race,
            identity.kingdom,
            identity.player.takeIf(String::isNotBlank)?.let { "Jogador: $it" }.orEmpty(),
        ).filter(String::isNotBlank).joinToString(" | ").ifBlank { "Ficha local" }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpen),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            SavedCharacterImage(
                image = character.image,
                contentDescription = identity.name.ifBlank { "Personagem" },
                modifier =
                    Modifier
                        .width(92.dp)
                        .height(116.dp),
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = identity.name.ifBlank { "Personagem sem nome" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CharacterMetric("PV", stored.session.currentHitPoints.toString())
                    CharacterMetric("PF", stored.session.currentFatiguePoints.toString())
                    CharacterMetric("PM", stored.session.currentManaPoints.toString())
                }

                Text(
                    text = stored.importMetadata.sourceFileName.ifBlank { "Ficha importada" },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onOpen,
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(38.dp),
                        contentPadding = ButtonDefaults.ContentPadding,
                    ) {
                        Text("Abrir", style = MaterialTheme.typography.labelMedium)
                    }
                    OutlinedButton(
                        onClick = onDelete,
                        modifier =
                            Modifier
                                .weight(1f)
                                .height(38.dp),
                        contentPadding = ButtonDefaults.ContentPadding,
                    ) {
                        Text("Excluir", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun CharacterMetric(
    label: String,
    value: String,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(6.dp),
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier =
                Modifier
                    .width(48.dp)
                    .padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun SavedCharacterImage(
    image: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val imageModel = remember(image) { image.toCharacterImageModel() }
    val shape = RoundedCornerShape(8.dp)

    if (imageModel == null) {
        DefaultSavedCharacterImage(
            contentDescription = contentDescription,
            modifier = modifier.clip(shape),
        )
        return
    }

    SubcomposeAsyncImage(
        model = imageModel,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier.clip(shape),
        loading = {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.size(28.dp))
            }
        },
        error = {
            DefaultSavedCharacterImage(
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
            )
        },
        success = {
            SubcomposeAsyncImageContent()
        },
    )
}

@Composable
private fun DefaultSavedCharacterImage(
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun EmptyCharacterListCard(onImportCharacter: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Nenhum personagem salvo neste aparelho.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Importe uma ficha do Drive para ela aparecer aqui com imagem, status e dados de sessao.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = onImportCharacter,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Buscar no Drive")
            }
        }
    }
}

private fun String.toCharacterImageModel(): Any? {
    val value = trim()
    if (value.isBlank()) return null
    return when {
        value.startsWith("/") || Regex("""^[A-Za-z]:[\\/].*""").matches(value) -> File(value)
        else -> value
    }
}
