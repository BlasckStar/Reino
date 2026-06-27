package com.blasck.reino.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.blasck.reino.presentation.components.KingdomInlineLoading
import com.blasck.reino.presentation.search.SearchCategory
import com.blasck.reino.presentation.search.SearchEntry
import com.blasck.reino.presentation.search.normalizeSearch
import com.blasck.reino.presentation.viewmodel.CharacterSearchViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CharacterSearchScreen(
    characterId: Long,
    viewModel: CharacterSearchViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(characterId) { viewModel.load(characterId) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = state.query,
            onValueChange = viewModel::updateQuery,
            label = { Text("Buscar na ficha") },
            placeholder = { Text("Ex.: esgrima, armadura, cura") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = state.selectedCategory == null,
                    onClick = { viewModel.selectCategory(null) },
                    label = { Text("Tudo") },
                )
            }
            items(SearchCategory.entries) { category ->
                FilterChip(
                    selected = state.selectedCategory == category,
                    onClick = { viewModel.selectCategory(category) },
                    label = { Text(category.label) },
                )
            }
        }

        if (state.query.length < 2 && state.recentQueries.isNotEmpty()) {
            Text("Buscas recentes", style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.recentQueries) { query ->
                    AssistChip(
                        onClick = { viewModel.useRecent(query) },
                        label = { Text(query) },
                    )
                }
            }
        }

        when {
            state.loading -> KingdomInlineLoading(message = "Consultando o grimorio...")
            state.query.length < 2 ->
                SearchMessage("Digite pelo menos dois caracteres para pesquisar.")
            state.results.isEmpty() -> SearchMessage("Nenhum resultado encontrado.")
            else ->
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    state.results.groupBy(SearchEntry::category).forEach { (category, entries) ->
                        item {
                            Text(
                                category.label,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(top = 8.dp),
                            )
                        }
                        items(entries, key = SearchEntry::id) { entry ->
                            SearchResultCard(
                                entry = entry,
                                query = state.query,
                                favorite = entry.id in state.favoriteIds,
                                onOpen = { viewModel.selectEntry(entry) },
                                onFavorite = { viewModel.toggleFavorite(entry) },
                            )
                        }
                    }
                }
        }
    }

    state.selectedEntry?.let { entry ->
        AlertDialog(
            onDismissRequest = viewModel::dismissDetails,
            title = { Text(entry.title) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(entry.category.label, color = MaterialTheme.colorScheme.primary)
                    if (entry.subtitle.isNotBlank()) Text(entry.subtitle)
                    if (entry.details.isNotBlank()) Text(entry.details)
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::dismissDetails) { Text("Fechar") }
            },
        )
    }
}

@Composable
private fun SearchResultCard(
    entry: SearchEntry,
    query: String,
    favorite: Boolean,
    onOpen: () -> Unit,
    onFavorite: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp).clickable(onClick = onOpen),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = highlighted(entry.title, query),
                    style = MaterialTheme.typography.titleMedium,
                )
                if (entry.subtitle.isNotBlank()) {
                    Text(entry.subtitle, style = MaterialTheme.typography.bodySmall)
                }
            }
            TextButton(onClick = onFavorite) {
                Text(if (favorite) "★" else "☆")
            }
        }
    }
}

@Composable
private fun SearchMessage(message: String) {
    Text(
        message,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(24.dp),
    )
}

private fun highlighted(
    text: String,
    query: String,
) = buildAnnotatedString {
    append(text)
    val normalizedText = text.normalizeSearch()
    val normalizedQuery = query.normalizeSearch()
    val start = normalizedText.indexOf(normalizedQuery)
    if (start >= 0 && normalizedQuery.isNotEmpty()) {
        addStyle(
            SpanStyle(
                background = androidx.compose.ui.graphics.Color.Yellow.copy(alpha = 0.35f),
                fontWeight = FontWeight.Bold,
            ),
            start,
            (start + normalizedQuery.length).coerceAtMost(text.length),
        )
    }
}
