package com.blasck.reino.presentation.search

import com.blasck.reino.presentation.screen.model.CharacterModel
import java.text.Normalizer

fun CharacterModel.toSearchEntries(characterId: Long): List<SearchEntry> =
    buildList {
        expertise.list.forEachIndexed { index, item ->
            add(
                SearchEntry(
                    id = "skill-$index",
                    characterId = characterId,
                    category = SearchCategory.SKILL,
                    title = item.name,
                    subtitle = "NH ${item.nh} • ${item.difficultType}/${item.difficultLevel}",
                    details = item.description,
                    keywords = listOf(item.cost, item.difficultExtra),
                ),
            )
        }
        raceAndAdvantages.list.forEachIndexed { index, item ->
            add(SearchEntry("advantage-$index", characterId, SearchCategory.ADVANTAGE, item.name, item.cost, item.description))
        }
        disadvantagesAndPeculiarities.list.forEachIndexed { index, item ->
            add(SearchEntry("disadvantage-$index", characterId, SearchCategory.DISADVANTAGE, item.name, item.cost, item.description))
        }
        meleeWeapon.list.forEachIndexed { index, item ->
            add(SearchEntry("melee-$index", characterId, SearchCategory.WEAPON, item.name, item.gdp, listOf(item.quality, item.gdpType, item.bal, item.notes).joinToString(" • ")))
        }
        rangedWeapon.list.forEachIndexed { index, item ->
            add(SearchEntry("ranged-$index", characterId, SearchCategory.WEAPON, item.name, item.damage, item.notes, listOf(item.precision, item.maxDistance)))
        }
        inventory.list.forEachIndexed { index, item ->
            add(SearchEntry("inventory-$index", characterId, SearchCategory.EQUIPMENT, item.name, item.weight, item.description, listOf(item.quantity, item.value)))
        }
        armorList.list.forEachIndexed { index, item ->
            add(SearchEntry("armor-$index", characterId, SearchCategory.ARMOR, item.name, "RD ${item.defenseRD}", item.description, listOf(item.defenseDP, item.weight)))
        }
        magicItems.list.forEachIndexed { index, item ->
            add(SearchEntry("magic-item-$index", characterId, SearchCategory.MAGIC_ITEM, item.name, "Mana ${item.mana}", item.description))
        }
        spellbook.list.forEachIndexed { index, item ->
            add(SearchEntry("spell-$index", characterId, SearchCategory.SPELL, item.name, "NH ${item.level}", item.notes, listOf(item.difficulty, item.cost)))
        }
        annotations.list.forEachIndexed { index, note ->
            add(SearchEntry("note-$index", characterId, SearchCategory.NOTE, note.take(60), details = note))
        }
    }

fun SearchEntry.matches(query: String): Boolean {
    val normalizedQuery = query.normalizeSearch()
    if (normalizedQuery.length < 2) return false
    return searchableText().normalizeSearch().contains(normalizedQuery)
}

fun String.normalizeSearch(): String =
    Normalizer.normalize(lowercase(), Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")

private fun SearchEntry.searchableText(): String =
    listOf(title, subtitle, details, keywords.joinToString(" ")).joinToString(" ")
