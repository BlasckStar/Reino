package com.blasck.reino.presentation.mapper

import com.blasck.reino.domain.model.CharacterImportMetadata
import com.blasck.reino.domain.model.CharacterSession
import com.blasck.reino.domain.model.CharacterSheetFormat
import com.blasck.reino.domain.model.DisadvantagesAndPeculiarities
import com.blasck.reino.domain.model.RaceAndAdvantages
import com.blasck.reino.domain.model.Spellbook
import com.blasck.reino.domain.model.StoredCharacter
import com.blasck.reino.fixtures.SyrioExpectedData
import org.junit.Assert.assertEquals
import org.junit.Test

class CharacterPresentationMapperTest {
    @Test
    fun `uses current session values without changing imported maximums`() {
        val stored =
            StoredCharacter(
                id = 1,
                character = SyrioExpectedData.character,
                session =
                    CharacterSession(
                        currentHitPoints = 8,
                        currentFatiguePoints = 9,
                        currentManaPoints = 10,
                        notes = "Ferido durante a sessão",
                    ),
                importMetadata =
                    CharacterImportMetadata(
                        sourceFileName = "FichaSyrio.xlsx",
                        sheetFormat = CharacterSheetFormat.REINO_V1,
                        importedAtEpochMillis = 1,
                        updatedAtEpochMillis = 1,
                    ),
            )

        val presentation = stored.toPresentationModel()

        assertEquals("8", presentation.status.hitPoints)
        assertEquals("9", presentation.status.fatigue)
        assertEquals("10", presentation.status.mana)
        assertEquals("Ferido durante a sessão", presentation.annotations.list.last())
        assertEquals("17", presentation.status.strength)
    }

    @Test
    fun `maps full spell information for combat tab`() {
        val stored =
            StoredCharacter(
                id = 1,
                character =
                    SyrioExpectedData.character.copy(
                        spellbook =
                            Spellbook(
                                list =
                                    listOf(
                                        Spellbook.SpellModel(
                                            name = "Cura",
                                            level = "15",
                                            difficulty = "IQ/D",
                                            cost = "2",
                                            page = "M89",
                                            spellClass = "Cura",
                                            duration = "Instantanea",
                                            castingCost = "2",
                                            maintenanceCost = "-",
                                            castingTime = "1s",
                                            notes = "Recupera PV.",
                                        ),
                                    ),
                            ),
                    ),
                session =
                    CharacterSession(
                        currentHitPoints = 8,
                        currentFatiguePoints = 9,
                        currentManaPoints = 10,
                    ),
                importMetadata =
                    CharacterImportMetadata(
                        sourceFileName = "FichaSyrio.xlsx",
                        sheetFormat = CharacterSheetFormat.REINO_V1,
                        importedAtEpochMillis = 1,
                        updatedAtEpochMillis = 1,
                    ),
            )

        val spell = stored.toPresentationModel().spellbook.list.single()

        assertEquals("Cura", spell.name)
        assertEquals("M89", spell.page)
        assertEquals("Cura", spell.spellClass)
        assertEquals("Instantanea", spell.duration)
        assertEquals("2", spell.castingCost)
        assertEquals("-", spell.maintenanceCost)
        assertEquals("1s", spell.castingTime)
    }

    @Test
    fun `keeps advantages disadvantages and peculiarities organized`() {
        val stored =
            StoredCharacter(
                id = 1,
                character =
                    SyrioExpectedData.character.copy(
                        raceAndAdvantages =
                            RaceAndAdvantages(
                                list =
                                    listOf(
                                        RaceAndAdvantages.AdvantageModel(
                                            name = "Reflexos em Combate",
                                            cost = "15",
                                            description = "Bonus em situacoes de combate.",
                                        ),
                                    ),
                            ),
                        disadvantagesAndPeculiarities =
                            DisadvantagesAndPeculiarities(
                                disadvantages =
                                    listOf(
                                        DisadvantagesAndPeculiarities.DPModel(
                                            cost = "-10",
                                            name = "Codigo de Honra",
                                            description = "Segue um codigo rigido.",
                                        ),
                                    ),
                                peculiarities =
                                    listOf(
                                        DisadvantagesAndPeculiarities.DPModel(
                                            cost = "-1",
                                            name = "Gosta de capas",
                                            description = "Sempre usa uma capa marcante.",
                                        ),
                                    ),
                            ),
                    ),
                session =
                    CharacterSession(
                        currentHitPoints = 8,
                        currentFatiguePoints = 9,
                        currentManaPoints = 10,
                    ),
                importMetadata =
                    CharacterImportMetadata(
                        sourceFileName = "FichaSyrio.xlsx",
                        sheetFormat = CharacterSheetFormat.REINO_V1,
                        importedAtEpochMillis = 1,
                        updatedAtEpochMillis = 1,
                    ),
            )

        val presentation = stored.toPresentationModel()

        assertEquals("Reflexos em Combate", presentation.raceAndAdvantages.list.single().name)
        assertEquals("Codigo de Honra", presentation.disadvantagesAndPeculiarities.disadvantages.single().name)
        assertEquals("Gosta de capas", presentation.disadvantagesAndPeculiarities.peculiarities.single().name)
        assertEquals(2, presentation.disadvantagesAndPeculiarities.list.size)
    }
}
