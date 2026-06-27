package com.blasck.reino.presentation.viewmodel

import com.blasck.reino.domain.drive.DriveCharacterEntry
import com.blasck.reino.domain.drive.characterKey
import com.blasck.reino.domain.model.StoredCharacter

class DriveCatalogItemMapper {
    fun map(
        entries: List<DriveCharacterEntry>,
        localCharacters: List<StoredCharacter>,
    ): List<DriveCatalogItem> =
        entries.map { entry ->
            val remoteSheetId = entry.primarySheet?.fileId.orEmpty()
            val local = localCharacters.firstOrNull { it.matches(entry, remoteSheetId) }
            DriveCatalogItem(
                entry = entry,
                localCharacterId = local?.id,
                labels = entry.labels(),
                state = entry.stateFor(local, remoteSheetId),
            )
        }

    private fun StoredCharacter.matches(
        entry: DriveCharacterEntry,
        remoteSheetId: String,
    ): Boolean =
        (remoteSheetId.isNotBlank() && importMetadata.remoteSheetFileId == remoteSheetId) ||
            importMetadata.sourceFileName.characterKey() == entry.key ||
            character.identity.name.characterKey() == entry.key

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

    private fun DriveCharacterEntry.stateFor(
        local: StoredCharacter?,
        remoteSheetId: String,
    ): DriveCatalogItemState =
        when {
            primarySheet == null -> DriveCatalogItemState.UNAVAILABLE
            local == null -> DriveCatalogItemState.NOT_IMPORTED
            remoteSheetId.isBlank() -> DriveCatalogItemState.IMPORTED
            local.importMetadata.remoteSheetFileId == remoteSheetId -> DriveCatalogItemState.UPDATED
            else -> DriveCatalogItemState.UPDATE_AVAILABLE
        }
}
