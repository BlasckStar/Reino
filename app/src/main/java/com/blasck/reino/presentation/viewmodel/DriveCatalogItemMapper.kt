package com.blasck.reino.presentation.viewmodel

import com.blasck.reino.domain.drive.DriveCharacterEntry
import com.blasck.reino.domain.drive.DriveRemoteFile
import com.blasck.reino.domain.drive.characterKey
import com.blasck.reino.domain.model.StoredCharacter

class DriveCatalogItemMapper {
    fun map(
        entries: List<DriveCharacterEntry>,
        localCharacters: List<StoredCharacter>,
    ): List<DriveCatalogItem> =
        entries.map { entry ->
            val remoteSheetId = entry.primarySheet?.fileId.orEmpty()
            val versions =
                entry.sheetVersions.mapIndexed { index, sheet ->
                    val local =
                        localCharacters.firstOrNull { it.matchesRemoteSheet(sheet.fileId) }
                            ?: localCharacters.firstOrNull {
                                index == 0 && it.matchesLegacyImport(entry)
                            }
                    DriveCatalogSheetVersion(
                        sheet = sheet,
                        localCharacterId = local?.id,
                        state = sheet.stateFor(local),
                    )
                }
            val local = versions.firstOrNull { it.localCharacterId != null }
            DriveCatalogItem(
                entry = entry,
                localCharacterId = local?.localCharacterId,
                versions = versions,
                labels = entry.labels(),
                state = versions.stateFor(entry, remoteSheetId),
            )
        }

    private fun StoredCharacter.matchesRemoteSheet(remoteSheetId: String): Boolean =
        remoteSheetId.isNotBlank() && importMetadata.remoteSheetFileId == remoteSheetId

    private fun StoredCharacter.matchesLegacyImport(
        entry: DriveCharacterEntry,
    ): Boolean =
        (importMetadata.remoteSheetFileId.isBlank() &&
            importMetadata.sourceFileName.characterKey() == entry.key) ||
            (importMetadata.remoteSheetFileId.isBlank() && character.identity.name.characterKey() == entry.key)

    private fun DriveCharacterEntry.labels(): List<String> =
        buildList {
            val hasSheet = primarySheet != null
            val hasImage = images.isNotEmpty()
            if (hasSheet != hasImage) add("Arquivo sem par")
            if (!hasSheet && hasImage) add("Imagem sem ficha")
            if (hasSheet && !hasImage) add("Imagem ausente")
            if (sheetVersions.size > 1) add("Multiplas versoes")
            if (images.size > 1) add("Multiplas imagens")
        }

    private fun DriveRemoteFile.stateFor(
        local: StoredCharacter?,
    ): DriveCatalogItemState =
        when {
            local == null -> DriveCatalogItemState.NOT_IMPORTED
            fileId.isBlank() -> DriveCatalogItemState.IMPORTED
            local.importMetadata.remoteSheetFileId == fileId -> DriveCatalogItemState.UPDATED
            else -> DriveCatalogItemState.UPDATE_AVAILABLE
        }

    private fun List<DriveCatalogSheetVersion>.stateFor(
        entry: DriveCharacterEntry,
        remoteSheetId: String,
    ): DriveCatalogItemState =
        when {
            entry.primarySheet == null -> DriveCatalogItemState.UNAVAILABLE
            isEmpty() -> DriveCatalogItemState.UNAVAILABLE
            all { it.state == DriveCatalogItemState.NOT_IMPORTED } -> DriveCatalogItemState.NOT_IMPORTED
            firstOrNull { it.sheet.fileId == remoteSheetId }?.state == DriveCatalogItemState.UPDATED ->
                DriveCatalogItemState.UPDATED
            any { it.state == DriveCatalogItemState.UPDATED || it.state == DriveCatalogItemState.IMPORTED } ->
                DriveCatalogItemState.IMPORTED
            else -> DriveCatalogItemState.UPDATE_AVAILABLE
        }
}
