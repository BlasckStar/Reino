package com.blasck.reino.fixtures

import com.blasck.reino.domain.model.Character
import com.blasck.reino.domain.model.CharacterAttributes
import com.blasck.reino.domain.model.CharacterIdentity

object SyrioExpectedData {
    val character =
        Character(
            identity =
                CharacterIdentity(
                    name = "Syrio Augusto",
                    player = "Luiz",
                    height = "1,75 m",
                    weight = "60 kg",
                    race = "Humana",
                    kingdom = "Justos",
                    age = "30",
                    pointsToSpend = 0.0,
                ),
            attributes =
                CharacterAttributes(
                    strength = 17,
                    dexterity = 21,
                    intelligence = 13,
                    health = 16,
                    fatiguePoints = 17,
                    manaPoints = 17,
                    will = 23,
                    perception = 13,
                    hitPoints = 19,
                    basicLift = 17.0,
                    basicSpeed = 11.25,
                    basicMove = 11,
                    dodge = 12,
                ),
        )
}
