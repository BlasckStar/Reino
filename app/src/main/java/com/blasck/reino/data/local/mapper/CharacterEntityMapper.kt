package com.blasck.reino.data.local.mapper

import com.blasck.reino.data.local.entity.CharacterEntity
import com.blasck.reino.domain.model.Character
import com.blasck.reino.domain.model.CharacterAttributes
import com.blasck.reino.domain.model.CharacterIdentity
import com.blasck.reino.domain.model.CharacterImportMetadata
import com.blasck.reino.domain.model.CharacterSession
import com.blasck.reino.domain.model.CharacterSheetFormat
import com.blasck.reino.domain.model.StoredCharacter
import com.google.gson.Gson

fun CharacterEntity.toDomain(gson: Gson = Gson()): StoredCharacter =
    StoredCharacter(
        id = id,
        character =
            characterJson.takeIf(String::isNotBlank)?.let {
                runCatching { gson.fromJson(it, Character::class.java) }.getOrNull()
            } ?: Character(
                identity =
                    CharacterIdentity(
                        name = name,
                        player = player,
                        creationDate = creationDate,
                        height = height,
                        weight = weight,
                        race = race,
                        kingdom = kingdom,
                        age = age,
                        pointsToSpend = pointsToSpend,
                        appearance = appearance,
                    ),
                attributes =
                    CharacterAttributes(
                        strength = strength,
                        dexterity = dexterity,
                        intelligence = intelligence,
                        health = health,
                        fatiguePoints = maximumFatiguePoints,
                        manaPoints = maximumManaPoints,
                        will = will,
                        perception = perception,
                        hitPoints = maximumHitPoints,
                        basicLift = basicLift,
                        basicSpeed = basicSpeed,
                        basicMove = basicMove,
                        dodge = dodge,
                    ),
            ),
        session =
            CharacterSession(
                currentHitPoints = currentHitPoints,
                currentFatiguePoints = currentFatiguePoints,
                currentManaPoints = currentManaPoints,
                notes = notes,
            ),
        importMetadata =
            CharacterImportMetadata(
                sourceFileName = sourceFileName,
                sheetFormat = CharacterSheetFormat.valueOf(sheetFormat),
                importedAtEpochMillis = importedAtEpochMillis,
                updatedAtEpochMillis = updatedAtEpochMillis,
                remoteSheetFileId = remoteSheetFileId,
                remoteImageFileId = remoteImageFileId,
            ),
    )

fun Character.toEntity(
    metadata: CharacterImportMetadata,
    id: Long = 0,
    session: CharacterSession =
        CharacterSession(
            currentHitPoints = attributes.hitPoints,
            currentFatiguePoints = attributes.fatiguePoints,
            currentManaPoints = attributes.manaPoints,
        ),
    gson: Gson = Gson(),
): CharacterEntity =
    CharacterEntity(
        id = id,
        name = identity.name,
        player = identity.player,
        creationDate = identity.creationDate,
        height = identity.height,
        weight = identity.weight,
        race = identity.race,
        kingdom = identity.kingdom,
        age = identity.age,
        pointsToSpend = identity.pointsToSpend,
        appearance = identity.appearance,
        strength = attributes.strength,
        dexterity = attributes.dexterity,
        intelligence = attributes.intelligence,
        health = attributes.health,
        maximumFatiguePoints = attributes.fatiguePoints,
        currentFatiguePoints = session.currentFatiguePoints,
        maximumManaPoints = attributes.manaPoints,
        currentManaPoints = session.currentManaPoints,
        will = attributes.will,
        perception = attributes.perception,
        maximumHitPoints = attributes.hitPoints,
        currentHitPoints = session.currentHitPoints,
        basicLift = attributes.basicLift,
        basicSpeed = attributes.basicSpeed,
        basicMove = attributes.basicMove,
        dodge = attributes.dodge,
        notes = session.notes,
        sourceFileName = metadata.sourceFileName,
        sheetFormat = metadata.sheetFormat.name,
        importedAtEpochMillis = metadata.importedAtEpochMillis,
        updatedAtEpochMillis = metadata.updatedAtEpochMillis,
        characterJson = gson.toJson(this),
        remoteSheetFileId = metadata.remoteSheetFileId,
        remoteImageFileId = metadata.remoteImageFileId,
    )
