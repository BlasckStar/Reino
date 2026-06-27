package com.blasck.reino.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blasck.reino.domain.repository.CharacterRepository
import com.blasck.reino.presentation.mapper.toPresentationModel
import com.blasck.reino.presentation.search.SearchCategory
import com.blasck.reino.presentation.search.SearchEntry
import com.blasck.reino.presentation.search.matches
import com.blasck.reino.presentation.search.toSearchEntries
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CharacterSearchViewModel(
    private val repository: CharacterRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(CharacterSearchUiState())
    val state: StateFlow<CharacterSearchUiState> = _state.asStateFlow()
    private var entries = emptyList<SearchEntry>()
    private var searchJob: Job? = null

    fun load(characterId: Long) {
        viewModelScope.launch {
            val stored = repository.findById(characterId)
            entries = stored?.toPresentationModel()?.toSearchEntries(characterId).orEmpty()
            _state.value = _state.value.copy(loading = false)
        }
    }

    fun updateQuery(query: String) {
        _state.value = _state.value.copy(query = query)
        searchJob?.cancel()
        searchJob =
            viewModelScope.launch {
                delay(250)
                applySearch()
            }
    }

    fun selectCategory(category: SearchCategory?) {
        _state.value = _state.value.copy(selectedCategory = category)
        applySearch()
    }

    fun selectEntry(entry: SearchEntry) {
        val query = _state.value.query.trim()
        val recent =
            if (query.length >= 2) {
                (listOf(query) + _state.value.recentQueries.filterNot { it.equals(query, true) })
                    .take(5)
            } else {
                _state.value.recentQueries
            }
        _state.value = _state.value.copy(selectedEntry = entry, recentQueries = recent)
    }

    fun dismissDetails() {
        _state.value = _state.value.copy(selectedEntry = null)
    }

    fun toggleFavorite(entry: SearchEntry) {
        val favorites = _state.value.favoriteIds.toMutableSet()
        if (!favorites.add(entry.id)) favorites.remove(entry.id)
        _state.value = _state.value.copy(favoriteIds = favorites)
    }

    fun useRecent(query: String) = updateQuery(query)

    private fun applySearch() {
        val current = _state.value
        val results =
            entries.filter { entry ->
                entry.matches(current.query) &&
                    (current.selectedCategory == null || entry.category == current.selectedCategory)
            }
        _state.value = current.copy(results = results)
    }
}

data class CharacterSearchUiState(
    val loading: Boolean = true,
    val query: String = "",
    val selectedCategory: SearchCategory? = null,
    val results: List<SearchEntry> = emptyList(),
    val selectedEntry: SearchEntry? = null,
    val recentQueries: List<String> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
)
