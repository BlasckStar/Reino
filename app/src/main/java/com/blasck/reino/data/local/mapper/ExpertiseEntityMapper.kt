package com.blasck.reino.data.local.mapper

import com.blasck.reino.data.local.entity.ExpertiseEntity
import com.blasck.reino.domain.model.Expertise

fun Expertise.ExpertiseModel.toEntity(
    characterId: Long,
    position: Int,
): ExpertiseEntity =
    ExpertiseEntity(
        characterId = characterId,
        position = position,
        name = name,
        description = description,
        difficultType = difficultType,
        difficultLevel = difficultLevel,
        difficultExtra = difficultExtra,
        cost = cost,
        level = nh,
        modifiers = modifiers.joinToString(MODIFIER_SEPARATOR),
    )

fun ExpertiseEntity.toDomain(): Expertise.ExpertiseModel =
    Expertise.ExpertiseModel(
        name = name,
        description = description,
        difficultType = difficultType,
        difficultLevel = difficultLevel,
        difficultExtra = difficultExtra,
        cost = cost,
        nh = level,
        modifiers =
            modifiers
                .split(MODIFIER_SEPARATOR)
                .filter(String::isNotBlank),
    )

private const val MODIFIER_SEPARATOR = "\u001F"
