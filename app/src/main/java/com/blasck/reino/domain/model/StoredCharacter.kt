package com.blasck.reino.domain.model

data class StoredCharacter(
    val id: Long,
    val character: Character,
    val session: CharacterSession,
    val importMetadata: CharacterImportMetadata,
)

data class CharacterSession(
    val currentHitPoints: Int,
    val currentFatiguePoints: Int,
    val currentManaPoints: Int,
    val notes: String = "",
)

data class CharacterImportMetadata(
    val sourceFileName: String,
    val sheetFormat: CharacterSheetFormat,
    val importedAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
    val remoteSheetFileId: String = "",
    val remoteImageFileId: String = "",
)
