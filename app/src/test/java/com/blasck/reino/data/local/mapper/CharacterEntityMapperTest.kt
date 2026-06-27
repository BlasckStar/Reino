package com.blasck.reino.data.local.mapper

import com.blasck.reino.domain.model.CharacterImportMetadata
import com.blasck.reino.domain.model.CharacterSheetFormat
import com.blasck.reino.fixtures.SyrioExpectedData
import org.junit.Assert.assertEquals
import org.junit.Test

class CharacterEntityMapperTest {
    @Test
    fun `maps imported character and metadata to Room entity`() {
        val metadata =
            CharacterImportMetadata(
                sourceFileName = "FichaSyrio.xlsx",
                sheetFormat = CharacterSheetFormat.REINO_V1,
                importedAtEpochMillis = 1000,
                updatedAtEpochMillis = 1000,
                remoteSheetFileId = "remote-sheet-123",
                remoteImageFileId = "remote-image-123",
            )

        val entity = SyrioExpectedData.character.toEntity(metadata)
        val stored = entity.copy(id = 7).toDomain()

        assertEquals(7, stored.id)
        assertEquals(SyrioExpectedData.character, stored.character)
        assertEquals("FichaSyrio.xlsx", stored.importMetadata.sourceFileName)
        assertEquals(CharacterSheetFormat.REINO_V1, stored.importMetadata.sheetFormat)
        assertEquals("remote-sheet-123", stored.importMetadata.remoteSheetFileId)
        assertEquals("remote-image-123", stored.importMetadata.remoteImageFileId)
        assertEquals(19, stored.session.currentHitPoints)
        assertEquals(17, stored.session.currentFatiguePoints)
        assertEquals(17, stored.session.currentManaPoints)
    }
}
