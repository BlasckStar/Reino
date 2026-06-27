package com.blasck.reino.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "character_backups",
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntity::class,
            parentColumns = ["id"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("characterId")],
)
data class CharacterBackupEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val characterId: Long,
    val createdAtEpochMillis: Long,
    val sourceFileName: String,
    val sheetFormat: String,
    val importedAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
    val currentHitPoints: Int,
    val currentFatiguePoints: Int,
    val currentManaPoints: Int,
    val notes: String,
    val characterJson: String,
    val remoteSheetFileId: String,
    val remoteImageFileId: String,
)
