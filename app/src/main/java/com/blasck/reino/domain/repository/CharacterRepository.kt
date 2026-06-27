package com.blasck.reino.domain.repository

import com.blasck.reino.domain.model.Character
import com.blasck.reino.domain.model.CharacterImportMetadata
import com.blasck.reino.domain.model.CharacterSession
import com.blasck.reino.domain.model.StoredCharacter
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    fun observeAll(): Flow<List<StoredCharacter>>

    fun observeById(id: Long): Flow<StoredCharacter?>

    suspend fun findById(id: Long): StoredCharacter?

    suspend fun saveImportedCharacter(
        character: Character,
        metadata: CharacterImportMetadata,
    ): Long

    suspend fun updateImportedCharacter(
        characterId: Long,
        character: Character,
        metadata: CharacterImportMetadata,
    ): Boolean

    suspend fun hasBackup(characterId: Long): Boolean

    suspend fun restoreLatestBackup(characterId: Long): Boolean

    suspend fun updateSession(
        characterId: Long,
        session: CharacterSession,
    )

    suspend fun delete(characterId: Long)
}
