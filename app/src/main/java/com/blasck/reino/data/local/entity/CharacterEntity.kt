package com.blasck.reino.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val player: String,
    val creationDate: String,
    val height: String,
    val weight: String,
    val race: String,
    val kingdom: String,
    val age: String,
    val pointsToSpend: Double,
    val appearance: String,
    val strength: Int,
    val dexterity: Int,
    val intelligence: Int,
    val health: Int,
    val maximumFatiguePoints: Int,
    val currentFatiguePoints: Int,
    val maximumManaPoints: Int,
    val currentManaPoints: Int,
    val will: Int,
    val perception: Int,
    val maximumHitPoints: Int,
    val currentHitPoints: Int,
    val basicLift: Double,
    val basicSpeed: Double,
    val basicMove: Int,
    val dodge: Int,
    val notes: String = "",
    val sourceFileName: String,
    val sheetFormat: String,
    val importedAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
    val characterJson: String = "",
    val remoteSheetFileId: String = "",
    val remoteImageFileId: String = "",
)
