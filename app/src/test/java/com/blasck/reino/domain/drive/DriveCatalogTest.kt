package com.blasck.reino.domain.drive

import org.junit.Assert.assertEquals
import org.junit.Test

class DriveCatalogTest {
    @Test
    fun `drive folder parser accepts folder links query links and raw ids`() {
        val folderId = "1NePK9boHoeLuMhAB-726EV0QFtlbhrT8"

        assertEquals(
            folderId,
            DriveFolderLinkParser.extractFolderId("https://drive.google.com/drive/folders/$folderId?usp=sharing"),
        )
        assertEquals(
            folderId,
            DriveFolderLinkParser.extractFolderId("https://drive.google.com/open?id=$folderId"),
        )
        assertEquals(folderId, DriveFolderLinkParser.extractFolderId(folderId))
    }

    @Test
    fun `drive folder parser rejects invalid links`() {
        assertEquals(null, DriveFolderLinkParser.extractFolderId("https://drive.google.com/drive/my-drive"))
        assertEquals(null, DriveFolderLinkParser.extractFolderId("abc"))
    }

    @Test
    fun `drive folder detection accepts imagens folders`() {
        assertEquals(
            true,
            file(
                id = "images",
                name = "Imagens",
                mimeType = "application/vnd.google-apps.folder",
            ).isCharacterImagesFolder(),
        )
        assertEquals(
            true,
            file(
                id = "oldImages",
                name = "Imagens dos Personagens",
                mimeType = "application/vnd.google-apps.folder",
            ).isCharacterImagesFolder(),
        )
    }

    @Test
    fun `config builds public download and thumbnail urls`() {
        assertEquals(
            "https://drive.google.com/uc?export=download&id=file123",
            ReinoDriveConfig.downloadUrl("file123"),
        )
        assertEquals(
            "https://docs.google.com/spreadsheets/d/sheet123/export?format=xlsx",
            ReinoDriveConfig.spreadsheetExportUrl("sheet123"),
        )
        assertEquals(
            "https://drive.google.com/thumbnail?id=image123&sz=w800",
            ReinoDriveConfig.thumbnailUrl("image123"),
        )
    }

    @Test
    fun `google sheets are listed as sheets and exported as xlsx`() {
        val file =
            DriveRemoteFile(
                fileId = "1UU0_fFkpFc1EYMoYior86i4YOEmSVKtE6zazOeh7tM8",
                name = "Syrio V4.ods",
                mimeType = GOOGLE_SHEETS_MIME_TYPE,
            )

        assertEquals(DriveRemoteFileKind.SHEET, file.kind)
        assertEquals(
            "https://docs.google.com/spreadsheets/d/1UU0_fFkpFc1EYMoYior86i4YOEmSVKtE6zazOeh7tM8/export?format=xlsx",
            file.downloadUrl,
        )
    }

    @Test
    fun `parser extracts public Drive files from html rows`() {
        val html =
            """
            <div data-id="1PEYerVLCpiJ3iCR_xszHGZVRw7sw_LH3">
                <span data-tooltip="Anabiel V3,2.ods.xlsx Microsoft Excel">Anabiel</span>
                <span aria-label="Modified 25 de jun.">25 de jun.</span>
                <span data-tooltip="Size: 318 KB\nStorage used: 318 KB">318 KB</span>
            </div>
            <div data-id="1OpbEwGCGurN_BfAg0xMRvUEmR74Don7P">
                <span data-tooltip="Syrio 01.png Image">Syrio</span>
            </div>
            <div data-id="1jJnrCk59kk68ANWJrjJBOe_2O2kxnJcY">
                <span data-tooltip="Imagens dos Personagens Shared folder">Imagens</span>
            </div>
            """.trimIndent()

        val files = DrivePublicListingParser().parse(html)

        assertEquals(3, files.size)
        assertEquals(DriveRemoteFileKind.SHEET, files[0].kind)
        assertEquals("25 de jun.", files[0].modifiedLabel)
        assertEquals("318 KB", files[0].sizeLabel)
        assertEquals(DriveRemoteFileKind.IMAGE, files[1].kind)
        assertEquals("Syrio 01.png", files[1].name)
        assertEquals(DriveRemoteFileKind.FOLDER, files[2].kind)
    }

    @Test
    fun `parser extracts structured Google Sheets rows from public Drive html`() {
        val html =
            """
            [[null,"1UU0_fFkpFc1EYMoYior86i4YOEmSVKtE6zazOeh7tM8"],null,null,null,"application/vnd.google-apps.spreadsheet",null,null,null,null,null,null,1,null,null,null,[[2]],null,null,null,null,null,null,[0,"114"],null,[[[""],[null,["application/vnd.google-apps.spreadsheet"]],null,null,null,"Google Sheets"],null,[[16,null,[null,[[["Syrio V4.ods",null,1]]]],null,null,[[["Syrio V4.ods",null,1],["Google Sheets"]]],null,null,1,[[["Syrio V4.ods"],["Google Sheets"],["Shared"]]]],[2,null,[null,[[["25 de jun."]]]],null,null,null,null,null,1,[[["Modified"],["25 de jun."]]]],[1,null,[null,[[["681 KB"]]]],null,null,[[["Size: 681 KB\nStorage used: 681 KB"]]],null,null,1,[[["Size: 681 KB\nStorage used: 681 KB"]]]]]]
            """.trimIndent()

        val files = DrivePublicListingParser().parse(html)

        assertEquals(1, files.size)
        assertEquals("Syrio V4.ods", files[0].name)
        assertEquals(DriveRemoteFileKind.SHEET, files[0].kind)
        assertEquals("25 de jun.", files[0].modifiedLabel)
        assertEquals("681 KB", files[0].sizeLabel)
    }

    @Test
    fun `catalog groups sheets and images by normalized character name`() {
        val sheets =
            listOf(
                file("old", "Syrio V3,2.ods.xlsx"),
                file("new", "Syrio V4.ods.xlsx"),
                file("baldo", "Baldo V4.xlsx"),
            )
        val images =
            listOf(
                file("syrio2", "Syrio 02.png", "image/png"),
                file("syrio1", "Syrio 01.png", "image/png"),
                file("baldo1", "Baldo01.png", "image/png"),
            )

        val catalog = DriveCatalogBuilder().build(sheets, images)
        val syrio = checkNotNull(catalog.firstOrNull { it.key == "syrio" })
        val baldo = checkNotNull(catalog.firstOrNull { it.key == "baldo" })

        assertEquals("Syrio V4.ods.xlsx", syrio.primarySheet?.name)
        assertEquals("Syrio 01.png", syrio.primaryImage?.name)
        assertEquals(2, syrio.sheetVersions.size)
        assertEquals("Baldo V4.xlsx", baldo.primarySheet?.name)
        assertEquals("Baldo01.png", baldo.primaryImage?.name)
    }

    @Test
    fun `catalog groups ods versions under the character name`() {
        val catalog =
            DriveCatalogBuilder().build(
                sheets =
                    listOf(
                        file("old", "Baldo V3,2.ods", GOOGLE_SHEETS_MIME_TYPE),
                        file("new", "Baldo V4", GOOGLE_SHEETS_MIME_TYPE),
                    ),
                images = listOf(file("baldo1", "Baldo 01.png", "image/png")),
            )

        val baldo = checkNotNull(catalog.singleOrNull { it.key == "baldo" })

        assertEquals("Baldo", baldo.displayName)
        assertEquals("Baldo V4", baldo.primarySheet?.name)
        assertEquals("Baldo 01.png", baldo.primaryImage?.name)
        assertEquals(2, baldo.sheetVersions.size)
    }

    @Test
    fun `catalog keeps unmatched sheets and ignores images without sheets`() {
        val catalog =
            DriveCatalogBuilder().build(
                sheets = listOf(file("sheet", "Solo V4.xlsx")),
                images = listOf(file("image", "Retrato 01.png", "image/png")),
            )

        val solo = checkNotNull(catalog.firstOrNull { it.key == "solo" })

        assertEquals("Solo V4.xlsx", solo.primarySheet?.name)
        assertEquals(null, solo.primaryImage)
        assertEquals(null, catalog.firstOrNull { it.key == "retrato" })
    }

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
