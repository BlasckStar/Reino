package com.blasck.reino.data.repository

import com.blasck.reino.data.local.dao.CharacterDao
import androidx.room.withTransaction
import com.blasck.reino.data.local.ReinoDatabase
import com.blasck.reino.data.local.dao.CharacterBackupDao
import com.blasck.reino.data.local.dao.ExpertiseDao
import com.blasck.reino.data.local.entity.CharacterBackupEntity
import com.blasck.reino.data.local.mapper.toEntity
import com.blasck.reino.data.local.mapper.toDomain
import com.blasck.reino.domain.model.Character
import com.blasck.reino.domain.model.CharacterImportMetadata
import com.blasck.reino.domain.model.CharacterSession
import com.blasck.reino.domain.model.CharacterSheetFormat
import com.blasck.reino.domain.model.StoredCharacter
import com.blasck.reino.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import com.google.gson.Gson

class LocalCharacterRepository(
    private val characterDao: CharacterDao,
    private val expertiseDao: ExpertiseDao,
    private val backupDao: CharacterBackupDao,
    private val database: ReinoDatabase,
    private val gson: Gson,
) : CharacterRepository {
    override fun observeAll(): Flow<List<StoredCharacter>> =
        characterDao.observeAll().map { characters ->
            characters.map { entity ->
                entity.toDomain(gson).withExpertises(
                    expertiseDao.findByCharacterId(entity.id),
                )
            }
        }

    override fun observeById(id: Long): Flow<StoredCharacter?> =
        combine(
            characterDao.observeById(id),
            expertiseDao.observeByCharacterId(id),
        ) { character, expertises ->
            character?.toDomain(gson)?.withExpertises(expertises)
        }

    override suspend fun findById(id: Long): StoredCharacter? =
        characterDao.findById(id)?.toDomain(gson)?.withExpertises(
            expertiseDao.findByCharacterId(id),
        )

    override suspend fun saveImportedCharacter(
        character: Character,
        metadata: CharacterImportMetadata,
    ): Long =
        database.withTransaction {
            val existing =
                metadata.remoteSheetFileId
                    .takeIf(String::isNotBlank)
                    ?.let { characterDao.findByRemoteSheetFileId(it) }
                    ?: characterDao.findByIdentity(
                        name = character.identity.name,
                        player = character.identity.player,
                    ).takeIf { metadata.remoteSheetFileId.isBlank() }
            val characterId =
                if (existing == null) {
                    characterDao.insert(character.toEntity(metadata, gson = gson))
                } else {
                    characterDao.update(
                        character.toEntity(
                            metadata =
                                metadata.copy(
                                    importedAtEpochMillis = existing.importedAtEpochMillis,
                                ),
                            id = existing.id,
                            session =
                                CharacterSession(
                                    currentHitPoints = existing.currentHitPoints,
                                    currentFatiguePoints = existing.currentFatiguePoints,
                                    currentManaPoints = existing.currentManaPoints,
                                    notes = existing.notes,
                                ),
                            gson = gson,
                        ),
                    )
                    expertiseDao.deleteByCharacterId(existing.id)
                    existing.id
                }
            expertiseDao.insertAll(
                character.expertise.list.mapIndexed { index, expertise ->
                    expertise.toEntity(characterId, index)
                },
            )
            characterId
        }

    override suspend fun updateImportedCharacter(
        characterId: Long,
        character: Character,
        metadata: CharacterImportMetadata,
    ): Boolean =
        database.withTransaction {
            val existing = characterDao.findById(characterId) ?: return@withTransaction false
            backupDao.insert(existing.toBackupEntity(gson))
            characterDao.update(
                character.toEntity(
                    metadata =
                        metadata.copy(
                            importedAtEpochMillis = existing.importedAtEpochMillis,
                        ),
                    id = existing.id,
                    session =
                        CharacterSession(
                            currentHitPoints = existing.currentHitPoints,
                            currentFatiguePoints = existing.currentFatiguePoints,
                            currentManaPoints = existing.currentManaPoints,
                            notes = existing.notes,
                        ),
                    gson = gson,
                ),
            )
            expertiseDao.deleteByCharacterId(existing.id)
            expertiseDao.insertAll(
                character.expertise.list.mapIndexed { index, expertise ->
                    expertise.toEntity(existing.id, index)
                },
            )
            true
        }

    override suspend fun hasBackup(characterId: Long): Boolean =
        backupDao.countByCharacterId(characterId) > 0

    override suspend fun restoreLatestBackup(characterId: Long): Boolean =
        database.withTransaction {
            val backup = backupDao.findLatestByCharacterId(characterId) ?: return@withTransaction false
            val character =
                runCatching {
                    gson.fromJson(backup.characterJson, Character::class.java)
                }.getOrNull() ?: return@withTransaction false
            val metadata =
                                CharacterImportMetadata(
                                    sourceFileName = backup.sourceFileName,
                                    sheetFormat = CharacterSheetFormat.valueOf(backup.sheetFormat),
                                    importedAtEpochMillis = backup.importedAtEpochMillis,
                                    updatedAtEpochMillis = System.currentTimeMillis(),
                                    remoteSheetFileId = backup.remoteSheetFileId,
                                    remoteImageFileId = backup.remoteImageFileId,
                                )
            val session =
                CharacterSession(
                    currentHitPoints = backup.currentHitPoints,
                    currentFatiguePoints = backup.currentFatiguePoints,
                    currentManaPoints = backup.currentManaPoints,
                    notes = backup.notes,
                )

            characterDao.update(
                character.toEntity(
                    metadata = metadata,
                    id = characterId,
                    session = session,
                    gson = gson,
                ),
            )
            expertiseDao.deleteByCharacterId(characterId)
            expertiseDao.insertAll(
                character.expertise.list.mapIndexed { index, expertise ->
                    expertise.toEntity(characterId, index)
                },
            )
            true
        }

    override suspend fun updateSession(
        characterId: Long,
        session: CharacterSession,
    ) {
        val current = characterDao.findById(characterId) ?: return
        characterDao.update(
            current.copy(
                currentHitPoints = session.currentHitPoints,
                currentFatiguePoints = session.currentFatiguePoints,
                currentManaPoints = session.currentManaPoints,
                notes = session.notes,
            ),
        )
    }

    override suspend fun delete(characterId: Long) {
        characterDao.findById(characterId)?.let { characterDao.delete(it) }
    }

    private fun StoredCharacter.withExpertises(
        expertises: List<com.blasck.reino.data.local.entity.ExpertiseEntity>,
    ): StoredCharacter =
        copy(
            character =
                character.copy(
                    expertise =
                        com.blasck.reino.domain.model.Expertise(
                            list = expertises.map { it.toDomain() },
                            totalCost =
                                expertises.sumOf {
                                    it.cost.replace(",", ".").toDoubleOrNull() ?: 0.0
                                }.let { total ->
                                    if (total % 1.0 == 0.0) {
                                        total.toInt().toString()
                                    } else {
                                        total.toString()
                                    }
                                },
                        ),
                ),
        )
}

private fun com.blasck.reino.data.local.entity.CharacterEntity.toBackupEntity(
    gson: Gson,
): CharacterBackupEntity {
    val character =
        characterJson.takeIf(String::isNotBlank)
            ?: gson.toJson(toDomain(gson).character)
    return CharacterBackupEntity(
        characterId = id,
        createdAtEpochMillis = System.currentTimeMillis(),
        sourceFileName = sourceFileName,
        sheetFormat = sheetFormat,
        importedAtEpochMillis = importedAtEpochMillis,
        updatedAtEpochMillis = updatedAtEpochMillis,
        currentHitPoints = currentHitPoints,
        currentFatiguePoints = currentFatiguePoints,
        currentManaPoints = currentManaPoints,
        notes = notes,
        characterJson = character,
        remoteSheetFileId = remoteSheetFileId,
        remoteImageFileId = remoteImageFileId,
    )
}
