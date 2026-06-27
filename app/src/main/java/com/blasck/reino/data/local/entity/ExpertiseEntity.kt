package com.blasck.reino.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expertises",
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
data class ExpertiseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val characterId: Long,
    val position: Int,
    val name: String,
    val description: String,
    val difficultType: String,
    val difficultLevel: String,
    val difficultExtra: String,
    val cost: String,
    val level: String,
    val modifiers: String,
)
