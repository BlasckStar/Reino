package com.blasck.reino.presentation.search

enum class SearchCategory(val label: String) {
    SKILL("Perícias"),
    ADVANTAGE("Vantagens"),
    DISADVANTAGE("Desvantagens"),
    WEAPON("Armas"),
    EQUIPMENT("Equipamentos"),
    ARMOR("Armaduras"),
    MAGIC_ITEM("Itens mágicos"),
    SPELL("Magias"),
    NOTE("Anotações"),
}

data class SearchEntry(
    val id: String,
    val characterId: Long,
    val category: SearchCategory,
    val title: String,
    val subtitle: String = "",
    val details: String = "",
    val keywords: List<String> = emptyList(),
)
