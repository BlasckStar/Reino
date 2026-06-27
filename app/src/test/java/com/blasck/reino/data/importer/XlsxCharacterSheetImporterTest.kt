package com.blasck.reino.data.importer

import com.blasck.reino.domain.importer.CharacterSheetImportResult
import com.blasck.reino.domain.model.CharacterImportIssue
import com.blasck.reino.fixtures.SyrioExpectedData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class XlsxCharacterSheetImporterTest {
    private val importer = XlsxCharacterSheetImporter()

    @Test
    fun `imports basic information and attributes from Syrio sheet`() {
        val input =
            checkNotNull(javaClass.classLoader?.getResourceAsStream("FichaSyrio.xlsx")) {
                "FichaSyrio.xlsx não foi encontrada nos recursos de teste"
            }

        val result = input.use(importer::import)

        assertTrue("Resultado inesperado: $result", result is CharacterSheetImportResult.Success)
        val imported = (result as CharacterSheetImportResult.Success).character
        val expected = SyrioExpectedData.character

        assertEquals(expected.identity, imported.identity)
        assertEquals(expected.attributes.strength, imported.attributes.strength)
        assertEquals(expected.attributes.dexterity, imported.attributes.dexterity)
        assertEquals(expected.attributes.intelligence, imported.attributes.intelligence)
        assertEquals(expected.attributes.health, imported.attributes.health)
        assertEquals(expected.attributes.hitPoints, imported.attributes.hitPoints)
        assertEquals(ReinoSheetV1Fields.format, result.format)
    }

    @Test
    fun `rejects content that is not an xlsx file`() {
        val result = "arquivo inválido".byteInputStream().use(importer::import)

        assertTrue(result is CharacterSheetImportResult.Failure)
    }

    @Test
    fun `imports Syrio expertises`() {
        val input =
            checkNotNull(javaClass.classLoader?.getResourceAsStream("FichaSyrio.xlsx"))

        val result = input.use(importer::import) as CharacterSheetImportResult.Success
        val expertises = result.character.expertise

        assertTrue(expertises.list.size >= 30)
        assertEquals("Esgrima", expertises.list.first().name)
        assertEquals("80", expertises.list.first().cost)
        assertEquals("F", expertises.list.first().difficultType)
        assertEquals("M", expertises.list.first().difficultLevel)
        assertEquals("32", expertises.list.first().nh)
        assertEquals("201", expertises.totalCost)
    }

    @Test
    fun `imports all filled character categories without blank entries`() {
        val input =
            checkNotNull(javaClass.classLoader?.getResourceAsStream("FichaSyrio.xlsx"))
        val character =
            (input.use(importer::import) as CharacterSheetImportResult.Success).character

        assertTrue(character.raceAndAdvantages.list.isNotEmpty())
        assertTrue(character.raceAndAdvantages.list.none { it.name.isBlank() })
        assertTrue(character.disadvantagesAndPeculiarities.disadvantages.isNotEmpty())
        assertTrue(character.disadvantagesAndPeculiarities.peculiarities.isNotEmpty())
        assertTrue(character.rangedWeapons.none { it.name.isBlank() })
        assertTrue(character.meleeWeapons.none { it.name.isBlank() })
        assertTrue(character.inventory.list.none { it.name.isBlank() })
        assertTrue(character.magicItems.list.none { it.name.isBlank() })
        assertTrue(character.armorList.list.none { it.name.isBlank() })
        assertTrue(character.spellbook.list.none { it.name.isBlank() })
        assertEquals("686", character.pointResume.total)
        assertEquals("25", character.money.gold.quantity)
        assertEquals("8", character.money.silver.quantity)
        assertEquals("3", character.money.copper.quantity)
        assertEquals(6, character.attributes.defenses.size)
        assertEquals(5, character.attributes.weightLevels.size)
    }

    @Test
    fun `imports Baldo V4 with marker for broken dodge formula from Drive`() {
        val input =
            checkNotNull(javaClass.classLoader?.getResourceAsStream("BaldoV4.xlsx")) {
                "BaldoV4.xlsx nao foi encontrada nos recursos de teste"
            }

        val result = input.use(importer::import)

        assertTrue("Resultado inesperado: $result", result is CharacterSheetImportResult.Success)
        val character = (result as CharacterSheetImportResult.Success).character
        assertEquals("Baldo", character.identity.name)
        assertEquals(0, character.attributes.dodge)
        assertTrue(
            character.importIssues.contains(
                CharacterImportIssue(
                    fieldName = "Esquiva",
                    cellAddress = "E63",
                    rawValue = "#REF!",
                ),
            ),
        )
    }
}
