package com.blasck.reino.data.local.mapper

import com.blasck.reino.data.importer.XlsxCharacterSheetImporter
import com.blasck.reino.domain.importer.CharacterSheetImportResult
import com.blasck.reino.domain.model.CharacterImportMetadata
import com.blasck.reino.domain.model.CharacterSheetFormat
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FullCharacterPersistenceTest {
    @Test
    fun `preserves imported categories through Room entity JSON`() {
        val input =
            checkNotNull(javaClass.classLoader?.getResourceAsStream("FichaSyrio.xlsx"))
        val imported =
            (input.use(XlsxCharacterSheetImporter()::import) as CharacterSheetImportResult.Success)
                .character
        val metadata =
            CharacterImportMetadata(
                sourceFileName = "FichaSyrio.xlsx",
                sheetFormat = CharacterSheetFormat.REINO_V1,
                importedAtEpochMillis = 1,
                updatedAtEpochMillis = 1,
            )

        val restored =
            imported
                .toEntity(metadata, gson = Gson())
                .copy(id = 1)
                .toDomain(Gson())
                .character

        assertEquals(imported.expertise.list.size, restored.expertise.list.size)
        assertEquals(imported.raceAndAdvantages.list.size, restored.raceAndAdvantages.list.size)
        assertEquals(imported.inventory.list.size, restored.inventory.list.size)
        assertEquals(imported.spellbook.list.size, restored.spellbook.list.size)
        assertTrue(restored.expertise.list.isNotEmpty())
    }
}
