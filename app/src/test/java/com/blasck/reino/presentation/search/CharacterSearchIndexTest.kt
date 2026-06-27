package com.blasck.reino.presentation.search

import com.blasck.reino.domain.model.CharacterImportMetadata
import com.blasck.reino.domain.model.CharacterSession
import com.blasck.reino.domain.model.CharacterSheetFormat
import com.blasck.reino.domain.model.StoredCharacter
import com.blasck.reino.fixtures.SyrioExpectedData
import com.blasck.reino.presentation.mapper.toPresentationModel
import com.blasck.reino.presentation.screen.model.CharacterModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterSearchIndexTest {
    @Test
    fun `search ignores accents and letter case`() {
        assertTrue("Perícias".normalizeSearch().contains("pericias"))
        assertTrue(
            SearchEntry(
                id = "1",
                characterId = 1,
                category = SearchCategory.SKILL,
                title = "Dança Fiônica",
            ).matches("DANCA"),
        )
    }

    @Test
    fun `character index includes skill and note categories`() {
        val stored =
            StoredCharacter(
                id = 1,
                character = SyrioExpectedData.character,
                session = CharacterSession(19, 17, 17, "Anotação secreta"),
                importMetadata =
                    CharacterImportMetadata(
                        "FichaSyrio.xlsx",
                        CharacterSheetFormat.REINO_V1,
                        1,
                        1,
                    ),
            )
        val entries = stored.toPresentationModel().toSearchEntries(1)

        assertTrue(entries.any { it.category == SearchCategory.NOTE && it.matches("secreta") })
    }

    @Test
    fun `character index includes every searchable category`() {
        val entries = searchableCharacter().toSearchEntries(7)

        assertEquals(SearchCategory.entries.toSet(), entries.map { it.category }.toSet())
        assertTrue(entries.all { it.characterId == 7L })
    }

    @Test
    fun `search matches details subtitles and keywords`() {
        val entries = searchableCharacter().toSearchEntries(7)

        assertTrue(entries.any { it.category == SearchCategory.EQUIPMENT && it.matches("reliquia") })
        assertTrue(entries.any { it.category == SearchCategory.ARMOR && it.matches("rd 4") })
        assertTrue(entries.any { it.category == SearchCategory.WEAPON && it.matches("maxima") })
    }

    @Test
    fun `search requires at least two normalized characters`() {
        val entry =
            SearchEntry(
                id = "1",
                characterId = 1,
                category = SearchCategory.SKILL,
                title = "Esgrima",
            )

        assertFalse(entry.matches("e"))
        assertTrue(entry.matches("es"))
    }

    private fun searchableCharacter(): CharacterModel =
        CharacterModel(
            expertise =
                CharacterModel.Expertise(
                    list =
                        listOf(
                            CharacterModel.Expertise.ExpertiseModel(
                                name = "Esgrima",
                                description = "Ataque com espada",
                                difficultType = "DX",
                                difficultLevel = "Media",
                                difficultExtra = "Saque rapido",
                                cost = "4",
                                nh = "16",
                            ),
                        ),
                ),
            raceAndAdvantages =
                CharacterModel.RaceAndAdvantages(
                    list =
                        listOf(
                            CharacterModel.RaceAndAdvantages.AdvantageModel(
                                name = "Visao Noturna",
                                description = "Enxerga no escuro",
                                cost = "10",
                            ),
                        ),
                ),
            disadvantagesAndPeculiarities =
                CharacterModel.DisadvantagesAndPeculiarities(
                    list =
                        listOf(
                            CharacterModel.DisadvantagesAndPeculiarities.DPModel(
                                name = "Curiosidade",
                                description = "Investiga perigos",
                                cost = "-5",
                            ),
                        ),
                ),
            meleeWeapon =
                CharacterModel.MeleeWeapon(
                    list =
                        listOf(
                            CharacterModel.MeleeWeapon.MeleeWeaponModel(
                                name = "Espada Longa",
                                quality = "Boa",
                                gdp = "1d+2",
                                gdpType = "corte",
                                bal = "1d",
                                notes = "Arma principal",
                            ),
                        ),
                ),
            rangedWeapon =
                CharacterModel.RangedWeapon(
                    list =
                        listOf(
                            CharacterModel.RangedWeapon.RangedWeaponModel(
                                name = "Arco Curto",
                                damage = "1d",
                                precision = "2",
                                maxDistance = "Distancia maxima 100",
                                notes = "Flechas comuns",
                            ),
                        ),
                ),
            inventory =
                CharacterModel.Inventory(
                    list =
                        listOf(
                            CharacterModel.Inventory.InventoryModel(
                                name = "Corda",
                                quantity = "1",
                                value = "5",
                                weight = "2 kg",
                                description = "Reliquia util",
                            ),
                        ),
                ),
            armorList =
                CharacterModel.ArmorList(
                    list =
                        listOf(
                            CharacterModel.ArmorList.ArmorModel(
                                name = "Cota de Malha",
                                weight = "8 kg",
                                description = "Protege o tronco",
                                defenseRD = "4",
                                defenseDP = "2",
                            ),
                        ),
                ),
            magicItems =
                CharacterModel.MagicItems(
                    list =
                        listOf(
                            CharacterModel.MagicItems.MagicItemModel(
                                name = "Anel de Mana",
                                mana = "3",
                                description = "Reserva arcana",
                            ),
                        ),
                ),
            spellbook =
                CharacterModel.Spellbook(
                    list =
                        listOf(
                            CharacterModel.Spellbook.SpellModel(
                                name = "Cura Menor",
                                level = "15",
                                difficulty = "H",
                                cost = "2",
                                notes = "Recupera vida",
                            ),
                        ),
                ),
            annotations =
                CharacterModel.Annotations(
                    list = listOf("Mapa secreto da masmorra"),
                ),
        )
}
