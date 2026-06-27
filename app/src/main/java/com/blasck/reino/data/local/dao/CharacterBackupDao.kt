package com.blasck.reino.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.blasck.reino.data.local.entity.CharacterBackupEntity

@Dao
interface CharacterBackupDao {
    @Insert
    suspend fun insert(backup: CharacterBackupEntity): Long

    @Query(
        "SELECT * FROM character_backups " +
            "WHERE characterId = :characterId " +
            "ORDER BY createdAtEpochMillis DESC, id DESC LIMIT 1",
    )
    suspend fun findLatestByCharacterId(characterId: Long): CharacterBackupEntity?

    @Query("SELECT COUNT(*) FROM character_backups WHERE characterId = :characterId")
    suspend fun countByCharacterId(characterId: Long): Int
}
