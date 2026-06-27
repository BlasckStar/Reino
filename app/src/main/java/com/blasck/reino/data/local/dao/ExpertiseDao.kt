package com.blasck.reino.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.blasck.reino.data.local.entity.ExpertiseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpertiseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(expertises: List<ExpertiseEntity>)

    @Query("SELECT * FROM expertises WHERE characterId = :characterId ORDER BY position")
    fun observeByCharacterId(characterId: Long): Flow<List<ExpertiseEntity>>

    @Query("SELECT * FROM expertises WHERE characterId = :characterId ORDER BY position")
    suspend fun findByCharacterId(characterId: Long): List<ExpertiseEntity>

    @Query("DELETE FROM expertises WHERE characterId = :characterId")
    suspend fun deleteByCharacterId(characterId: Long)
}
