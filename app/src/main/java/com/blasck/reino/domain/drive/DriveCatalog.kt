package com.blasck.reino.domain.drive

import java.text.Normalizer

object ReinoDriveConfig {
    const val ROOT_FOLDER_ID = "1NePK9boHoeLuMhAB-726EV0QFtlbhrT8"
    const val CHARACTER_IMAGES_FOLDER_ID = "1jJnrCk59kk68ANWJrjJBOe_2O2kxnJcY"

    fun folderUrl(folderId: String): String =
        "https://drive.google.com/drive/folders/$folderId"

    fun downloadUrl(fileId: String): String =
        "https://drive.google.com/uc?export=download&id=$fileId"

    fun spreadsheetExportUrl(fileId: String): String =
        "https://docs.google.com/spreadsheets/d/$fileId/export?format=xlsx"

    fun thumbnailUrl(
        fileId: String,
        size: Int = 800,
    ): String = "https://drive.google.com/thumbnail?id=$fileId&sz=w$size"
}

data class DriveSourceConfig(
    val rootFolderId: String = ReinoDriveConfig.ROOT_FOLDER_ID,
    val customLink: String = "",
) {
    val isDefault: Boolean =
        rootFolderId == ReinoDriveConfig.ROOT_FOLDER_ID && customLink.isBlank()
}

object DriveFolderLinkParser {
    private val folderPathRegex = Regex("""/folders/([A-Za-z0-9_-]{20,})""")
    private val idQueryRegex = Regex("""[?&]id=([A-Za-z0-9_-]{20,})""")
    private val rawIdRegex = Regex("""^[A-Za-z0-9_-]{20,}$""")

    fun extractFolderId(input: String): String? {
        val value = input.trim()
        if (value.isBlank()) return null
        return folderPathRegex.find(value)?.groupValues?.get(1)
            ?: idQueryRegex.find(value)?.groupValues?.get(1)
            ?: value.takeIf { rawIdRegex.matches(it) }
    }
}

data class DriveRemoteFile(
    val fileId: String,
    val name: String,
    val mimeType: String,
    val modifiedLabel: String = "",
    val sizeLabel: String = "",
) {
    val kind: DriveRemoteFileKind = DriveRemoteFileKind.from(name, mimeType)
    val downloadUrl: String =
        if (mimeType == GOOGLE_SHEETS_MIME_TYPE) {
            ReinoDriveConfig.spreadsheetExportUrl(fileId)
        } else {
            ReinoDriveConfig.downloadUrl(fileId)
        }
    val thumbnailUrl: String? =
        if (kind == DriveRemoteFileKind.IMAGE) ReinoDriveConfig.thumbnailUrl(fileId) else null
}

fun DriveRemoteFile.isCharacterImagesFolder(): Boolean =
    kind == DriveRemoteFileKind.FOLDER &&
        name.normalizedDriveName() in setOf("imagens", "imagens dos personagens")

enum class DriveRemoteFileKind {
    SHEET,
    IMAGE,
    FOLDER,
    OTHER,
    ;

    companion object {
        fun from(
            name: String,
            mimeType: String,
        ): DriveRemoteFileKind =
            when {
                mimeType == "application/vnd.google-apps.folder" -> FOLDER
                mimeType == GOOGLE_SHEETS_MIME_TYPE -> SHEET
                mimeType.startsWith("image/") || name.hasAnyExtension("png", "jpg", "jpeg") -> IMAGE
                name.hasAnyExtension("xlsx", "ods") -> SHEET
                else -> OTHER
            }
    }
}

data class DriveCharacterEntry(
    val key: String,
    val displayName: String,
    val primarySheet: DriveRemoteFile?,
    val sheetVersions: List<DriveRemoteFile>,
    val primaryImage: DriveRemoteFile?,
    val images: List<DriveRemoteFile>,
)

class DriveCatalogBuilder {
    fun build(
        sheets: List<DriveRemoteFile>,
        images: List<DriveRemoteFile>,
    ): List<DriveCharacterEntry> {
        val sheetsByCharacter =
            sheets
                .filter { it.kind == DriveRemoteFileKind.SHEET }
                .groupBy { it.name.characterKey() }
        val imagesByCharacter =
            images
                .filter { it.kind == DriveRemoteFileKind.IMAGE }
                .groupBy { it.name.characterKey() }

        return sheetsByCharacter.keys
            .filter(String::isNotBlank)
            .sorted()
            .map { key ->
                val sheetVersions = sheetsByCharacter[key].orEmpty().sortedWith(sheetComparator)
                val imageVersions = imagesByCharacter[key].orEmpty().sortedWith(imageComparator)
                DriveCharacterEntry(
                    key = key,
                    displayName = sheetVersions.firstOrNull()?.name?.characterDisplayNameFromFile()
                        ?: imageVersions.firstOrNull()?.name?.characterDisplayNameFromFile()
                        ?: key,
                    primarySheet = sheetVersions.firstOrNull(),
                    sheetVersions = sheetVersions,
                    primaryImage = imageVersions.firstOrNull(),
                    images = imageVersions,
                )
            }
    }

    private val sheetComparator =
        compareByDescending<DriveRemoteFile> { it.name.sheetVersionRank() }
            .thenBy { it.name }

    private val imageComparator =
        compareBy<DriveRemoteFile> { it.name.imageRank() }
            .thenBy { it.name }
}

class DrivePublicListingParser {
    fun parse(html: String): List<DriveRemoteFile> {
        val idRegex = Regex("data-id=\"([A-Za-z0-9_-]{25,})\"")
        val tooltipFiles =
            idRegex.findAll(html)
            .mapNotNull { match ->
                val id = match.groupValues[1]
                val context = html.substring(match.range.first, (match.range.first + 4_500).coerceAtMost(html.length))
                val tooltip =
                    Regex("data-tooltip=\"([^\"]+)\"")
                        .find(context)
                        ?.groupValues
                        ?.get(1)
                        ?.decodeHtmlEntities()
                        ?: return@mapNotNull null
                val name = tooltip.fileNameFromTooltip() ?: return@mapNotNull null
                val mime = tooltip.mimeTypeFromTooltip(name)
                DriveRemoteFile(
                    fileId = id,
                    name = name,
                    mimeType = mime,
                    modifiedLabel =
                        Regex("aria-label=\"Modified ([^\"]+)\"")
                            .find(context)
                            ?.groupValues
                            ?.get(1)
                            .orEmpty(),
                    sizeLabel =
                        Regex("""Size: ([^"\\]+)""")
                            .find(context)
                            ?.groupValues
                            ?.get(1)
                            ?.substringBefore("\\n")
                            .orEmpty(),
                )
            }
        val structuredFiles = parseStructuredFiles(html)
        return (tooltipFiles + structuredFiles)
            .distinctBy { it.fileId }
            .toList()
    }

    private fun parseStructuredFiles(html: String): Sequence<DriveRemoteFile> {
        val fileStartRegex =
            Regex("""\[\[null,"([A-Za-z0-9_-]{25,})"\],null,null,null,"([^"]+)"""")
        return fileStartRegex.findAll(html)
            .mapNotNull { match ->
                val id = match.groupValues[1]
                val mimeType = match.groupValues[2]
                val context = html.substring(match.range.first, (match.range.first + 5_500).coerceAtMost(html.length))
                val name =
                    Regex("""\[\[\["([^"]+)",null,1\]\]""")
                        .find(context)
                        ?.groupValues
                        ?.get(1)
                        ?.decodeHtmlEntities()
                        ?: return@mapNotNull null
                DriveRemoteFile(
                    fileId = id,
                    name = name,
                    mimeType = mimeType,
                    modifiedLabel =
                        Regex("""\[\[\["Modified"\],\["([^"]+)"\]\]\]""")
                            .find(context)
                            ?.groupValues
                            ?.get(1)
                            ?.decodeHtmlEntities()
                            .orEmpty(),
                    sizeLabel =
                        Regex("""\[\[\["Size: ([^"\\]+)""")
                            .find(context)
                            ?.groupValues
                            ?.get(1)
                            ?.decodeHtmlEntities()
                            .orEmpty(),
                )
            }
    }
}

fun String.characterKey(): String =
    characterDisplayNameFromFile().normalizedDriveName()

fun String.characterDisplayNameFromFile(): String =
    displayNameFromFile()
        .replace(Regex("""\bV\d+(?:,\d+)?\b""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""(?<=\D)\d+\b"""), "")
        .replace(Regex("""\b\d+\b"""), "")
        .replace(Regex("""\bsem fundo\b""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""\bpreview\b""", RegexOption.IGNORE_CASE), "")
        .trim()

fun String.displayNameFromFile(): String =
    replace(Regex("""\.ods\.xlsx$""", RegexOption.IGNORE_CASE), "")
        .replace(Regex("""\.(ods|xlsx|png|jpg|jpeg)$""", RegexOption.IGNORE_CASE), "")
        .trim()

private fun String.sheetVersionRank(): Int =
    Regex("""\bV(\d+)(?:,(\d+))?\b""", RegexOption.IGNORE_CASE)
        .find(this)
        ?.let { match ->
            val major = match.groupValues[1].toIntOrNull() ?: 0
            val minor = match.groupValues.getOrNull(2)?.toIntOrNull() ?: 0
            major * 100 + minor
        } ?: 0

private fun String.imageRank(): Int =
    when {
        Regex("""\b01\b""").containsMatchIn(this) -> 0
        contains("preview", ignoreCase = true) -> 1
        else -> 2
    }

private fun String.normalizedDriveName(): String =
    Normalizer.normalize(trim().lowercase(), Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
        .replace(Regex("""[^a-z0-9]+"""), " ")
        .trim()

private fun String.hasAnyExtension(vararg extensions: String): Boolean =
    extensions.any { extension -> endsWith(".$extension", ignoreCase = true) }

private fun String.fileNameFromTooltip(): String? =
    when {
        contains(" Microsoft Excel") -> substringBefore(" Microsoft Excel")
        contains(" Shared folder") -> substringBefore(" Shared folder")
        contains(" Image") -> substringBefore(" Image")
        contains(" PNG") -> substringBefore(" PNG")
        contains(" JPEG") -> substringBefore(" JPEG")
        contains(" JPG") -> substringBefore(" JPG")
        hasAnyExtension("png", "jpg", "jpeg", "ods", "xlsx") -> this
        else -> null
    }?.trim()?.takeIf(String::isNotBlank)

private fun String.mimeTypeFromTooltip(name: String): String =
    when {
        contains("Shared folder") -> "application/vnd.google-apps.folder"
        contains("Google Sheets") -> GOOGLE_SHEETS_MIME_TYPE
        name.endsWith(".png", ignoreCase = true) -> "image/png"
        name.endsWith(".jpg", ignoreCase = true) || name.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
        name.endsWith(".xlsx", ignoreCase = true) -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        name.endsWith(".ods", ignoreCase = true) -> GOOGLE_SHEETS_MIME_TYPE
        else -> "application/octet-stream"
    }

private fun String.decodeHtmlEntities(): String =
    replace("&amp;", "&")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .replace("&lt;", "<")
        .replace("&gt;", ">")

const val GOOGLE_SHEETS_MIME_TYPE = "application/vnd.google-apps.spreadsheet"
