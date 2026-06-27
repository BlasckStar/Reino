package com.blasck.reino.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.blasck.reino.R
import com.blasck.reino.domain.drive.DriveRemoteFile
import com.blasck.reino.presentation.components.KingdomInlineLoading
import com.blasck.reino.presentation.components.KingdomLoading
import com.blasck.reino.presentation.viewmodel.DriveCatalogItem
import com.blasck.reino.presentation.viewmodel.DriveCatalogItemState
import com.blasck.reino.presentation.viewmodel.DriveCatalogSheetVersion
import com.blasck.reino.presentation.viewmodel.DriveCatalogUiState
import com.blasck.reino.presentation.viewmodel.DriveCatalogViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DriveCatalogScreen(
    onImported: (Long, String) -> Unit,
    onOpenCharacter: (Long, String) -> Unit,
    onManualImport: (() -> Unit)? = null,
    stayOnListAfterImport: Boolean = false,
    title: String = "Lista de personagens",
    viewModel: DriveCatalogViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }
    LaunchedEffect(state) {
        val imported = state as? DriveCatalogUiState.Imported
        if (imported != null) {
            if (stayOnListAfterImport) {
                viewModel.refresh()
            } else {
                onImported(imported.characterId, imported.name)
            }
        }
    }

    when (val current = state) {
        DriveCatalogUiState.Loading ->
            ProgressLayout("Carregando personagens do Drive...")

        is DriveCatalogUiState.Importing ->
            ProgressLayout("Importando ${current.name}...")

        is DriveCatalogUiState.Error ->
            ErrorLayout(
                message = current.message,
                onRetry = viewModel::refresh,
            )

        is DriveCatalogUiState.Imported ->
            ProgressLayout("Ficha importada.")

        is DriveCatalogUiState.Ready ->
            DriveCatalogContent(
                items = current.items,
                title = title,
                onRefresh = viewModel::refresh,
                onManualImport = onManualImport,
                onOpenCharacter = onOpenCharacter,
                onImport = viewModel::importCharacter,
                onUpdate = viewModel::updateCharacter,
            )
    }
}

@Composable
private fun DriveCatalogContent(
    items: List<DriveCatalogItem>,
    title: String,
    onRefresh: () -> Unit,
    onManualImport: (() -> Unit)?,
    onOpenCharacter: (Long, String) -> Unit,
    onImport: (DriveCatalogItem) -> Unit,
    onUpdate: (DriveCatalogItem) -> Unit,
) {
    val selectedImages = remember { mutableStateMapOf<String, DriveRemoteFile>() }
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (onManualImport != null) {
                        Button(
                            onClick = onManualImport,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Importar personagem")
                        }
                    }
                    OutlinedButton(
                        onClick = onRefresh,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Atualizar")
                    }
                }
            }
        }

        if (items.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text("Nenhum personagem encontrado na pasta do Drive.")
            }
        } else {
            items(items, key = { it.entry.key }) { item ->
                DriveCharacterCard(
                    item = item,
                    selectedImage = selectedImages[item.entry.key] ?: item.entry.primaryImage,
                    onSelectedImage = { selectedImages[item.entry.key] = it },
                    onOpenCharacter = onOpenCharacter,
                    onImport = { version ->
                        onImport(
                            item.copy(
                                entry = item.entry.copy(
                                    primarySheet = version.sheet,
                                    primaryImage = selectedImages[item.entry.key] ?: item.entry.primaryImage,
                                ),
                            ),
                        )
                    },
                    onUpdate = { version ->
                        onUpdate(
                            item.copy(
                                localCharacterId = version.localCharacterId,
                                entry = item.entry.copy(
                                    primarySheet = version.sheet,
                                    primaryImage = selectedImages[item.entry.key] ?: item.entry.primaryImage,
                                ),
                            ),
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun DriveCharacterCard(
    item: DriveCatalogItem,
    selectedImage: DriveRemoteFile?,
    onSelectedImage: (DriveRemoteFile) -> Unit,
    onOpenCharacter: (Long, String) -> Unit,
    onImport: (DriveCatalogSheetVersion) -> Unit,
    onUpdate: (DriveCatalogSheetVersion) -> Unit,
) {
    val entry = item.entry
    var showImageDialog by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DriveCharacterImage(
                image = selectedImage,
                contentDescription = entry.displayName,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = entry.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = entry.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text =
                        listOf(
                            "${entry.sheetVersions.size} ficha(s)",
                            "${entry.images.size} imagem(ns)",
                            entry.primarySheet?.modifiedLabel.orEmpty().takeIf(String::isNotBlank),
                            entry.primarySheet?.sizeLabel.orEmpty().takeIf(String::isNotBlank),
                        ).filterNotNull().joinToString(" | "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = (listOf(item.state.label) + item.labels).joinToString(" | "),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (entry.images.size > 1) {
                    OutlinedButton(
                        onClick = { showImageDialog = true },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(40.dp),
                        contentPadding = ButtonDefaults.ContentPadding,
                    ) {
                        Text(
                            text = "Imagem",
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                        )
                    }
                }
                if (item.versions.isEmpty()) {
                    OutlinedButton(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                    ) {
                        Text("Sem ficha")
                    }
                } else {
                    item.versions.forEach { version ->
                        DriveVersionAction(
                            version = version,
                            onOpenCharacter = { id -> onOpenCharacter(id, entry.displayName) },
                            onImport = { onImport(version) },
                            onUpdate = { onUpdate(version) },
                        )
                    }
                }
            }
        }
    }

    if (showImageDialog) {
        AlertDialog(
            onDismissRequest = { showImageDialog = false },
            title = { Text("Escolher imagem") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    entry.images.forEach { image ->
                        TextButton(
                            onClick = {
                                onSelectedImage(image)
                                showImageDialog = false
                            },
                        ) {
                            Text(image.name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showImageDialog = false }) {
                    Text("Fechar")
                }
            },
        )
    }
}

@Composable
private fun DriveVersionAction(
    version: DriveCatalogSheetVersion,
    onOpenCharacter: (Long) -> Unit,
    onImport: () -> Unit,
    onUpdate: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = version.sheet.name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            val localId = version.localCharacterId
            if (localId != null) {
                OutlinedButton(
                    onClick = { onOpenCharacter(localId) },
                    modifier = Modifier.weight(1f).height(40.dp),
                    contentPadding = ButtonDefaults.ContentPadding,
                ) {
                    Text(
                        text = "Abrir",
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                    )
                }
            }
            Button(
                onClick =
                    when (version.state) {
                        DriveCatalogItemState.UNAVAILABLE,
                        DriveCatalogItemState.NOT_IMPORTED,
                        -> onImport

                        DriveCatalogItemState.IMPORTED,
                        DriveCatalogItemState.UPDATED,
                        DriveCatalogItemState.UPDATE_AVAILABLE,
                        -> onUpdate
                    },
                enabled = version.state != DriveCatalogItemState.UNAVAILABLE,
                modifier = Modifier.weight(1f).height(40.dp),
                contentPadding = ButtonDefaults.ContentPadding,
            ) {
                Text(
                    text =
                        when (version.state) {
                            DriveCatalogItemState.UNAVAILABLE -> "Sem ficha"
                            DriveCatalogItemState.NOT_IMPORTED -> "Importar"
                            DriveCatalogItemState.IMPORTED -> "Atualizar"
                            DriveCatalogItemState.UPDATED -> "Atualizar"
                            DriveCatalogItemState.UPDATE_AVAILABLE -> "Atualizar"
                        },
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun DriveCharacterImage(
    image: DriveRemoteFile?,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    if (image == null) {
        DefaultCharacterImage(
            contentDescription = contentDescription,
            modifier = modifier,
        )
        return
    }

    SubcomposeAsyncImage(
        model = image.downloadUrl,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier,
        loading = {
            LoadingCharacterImage(modifier = Modifier.fillMaxSize())
        },
        error = {
            DefaultCharacterImage(
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
private fun LoadingCharacterImage(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        KingdomInlineLoading(badgeSize = 56.dp)
    }
}

@Composable
private fun DefaultCharacterImage(
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp),
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
private fun ProgressLayout(message: String) {
    KingdomLoading(message = message)
}

@Composable
private fun ErrorLayout(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Nao foi possivel carregar o Drive",
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = message,
            modifier = Modifier.padding(top = 8.dp),
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Text("Tentar novamente")
        }
    }
}
