package com.blasck.reino.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.blasck.reino.data.local.entity.CharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(character: CharacterEntity): Long

    @Update
    suspend fun update(character: CharacterEntity)

    @Delete
    suspend fun delete(character: CharacterEntity)

    @Query("SELECT * FROM characters WHERE id = :id")
    fun observeById(id: Long): Flow<CharacterEntity?>

    @Query("SELECT * FROM characters ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun findById(id: Long): CharacterEntity?

    @Query(
        "SELECT * FROM characters " +
            "WHERE name = :name AND player = :player " +
            "ORDER BY id LIMIT 1",
    )
    suspend fun findByIdentity(
        name: String,
        player: String,
    ): CharacterEntity?
}
