package com.blasck.reino.presentation.viewmodel

import com.blasck.reino.domain.drive.DriveCatalogBuilder
import com.blasck.reino.domain.drive.DriveRemoteFile
import com.blasck.reino.domain.model.Character
import com.blasck.reino.domain.model.CharacterIdentity
import com.blasck.reino.domain.model.CharacterImportMetadata
import com.blasck.reino.domain.model.CharacterSession
import com.blasck.reino.domain.model.CharacterSheetFormat
import com.blasck.reino.domain.model.StoredCharacter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DriveCatalogItemMapperTest {
    private val mapper = DriveCatalogItemMapper()

    @Test
    fun `marks remote update when local character is linked to an older sheet`() {
        val entry =
            DriveCatalogBuilder().build(
                sheets =
                    listOf(
                        file("old-sheet", "Syrio V3.xlsx"),
                        file("new-sheet", "Syrio V4.xlsx"),
                    ),
                images = listOf(file("image", "Syrio 01.png", "image/png")),
            ).single()
        val local =
            storedCharacter(
                name = "Syrio",
                sourceFileName = "Syrio V3.xlsx",
                remoteSheetFileId = "old-sheet".padEnd(26, '0'),
            )

        val item = mapper.map(listOf(entry), listOf(local)).single()

        assertEquals(local.id, item.localCharacterId)
        assertEquals(DriveCatalogItemState.UPDATE_AVAILABLE, item.state)
        assertTrue(item.labels.contains("Multiplas versoes"))
    }

    @Test
    fun `marks matched current remote file as updated`() {
        val entry =
            DriveCatalogBuilder().build(
                sheets = listOf(file("new-sheet", "Syrio V4.xlsx")),
                images = listOf(file("image", "Syrio 01.png", "image/png")),
            ).single()
        val local =
            storedCharacter(
                name = "Syrio",
                sourceFileName = "Syrio V4.xlsx",
                remoteSheetFileId = "new-sheet".padEnd(26, '0'),
            )

        val item = mapper.map(listOf(entry), listOf(local)).single()

        assertEquals(local.id, item.localCharacterId)
        assertEquals(DriveCatalogItemState.UPDATED, item.state)
    }

    @Test
    fun `labels unmatched sheets and ignores image only entries`() {
        val entries =
            DriveCatalogBuilder().build(
                sheets = listOf(file("sheet", "Solo V4.xlsx")),
                images = listOf(file("image", "Retrato 01.png", "image/png")),
            )

        val items = mapper.map(entries, emptyList())
        val solo = checkNotNull(items.firstOrNull { it.entry.key == "solo" })

        assertEquals(DriveCatalogItemState.NOT_IMPORTED, solo.state)
        assertTrue(solo.labels.contains("Arquivo sem par"))
        assertTrue(solo.labels.contains("Imagem ausente"))
        assertEquals(null, items.firstOrNull { it.entry.key == "retrato" })
    }

    private fun storedCharacter(
        name: String,
        sourceFileName: String,
        remoteSheetFileId: String,
    ): StoredCharacter =
        StoredCharacter(
            id = 7,
            character = Character(identity = CharacterIdentity(name = name)),
            session =
                CharacterSession(
                    currentHitPoints = 10,
                    currentFatiguePoints = 10,
                    currentManaPoints = 10,
                    notes = "Notas da sessao preservadas no repositorio local",
                ),
            importMetadata =
                CharacterImportMetadata(
                    sourceFileName = sourceFileName,
                    sheetFormat = CharacterSheetFormat.REINO_V1,
                    importedAtEpochMillis = 100,
                    updatedAtEpochMillis = 200,
                    remoteSheetFileId = remoteSheetFileId,
                ),
        )

    private fun file(
        id: String,
        name: String,
        mimeType: String = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    ): DriveRemoteFile =
        DriveRemoteFile(
            fileId = id.padEnd(26, '0'),
            name = name,
            mimeType = mimeType,
        )
}
