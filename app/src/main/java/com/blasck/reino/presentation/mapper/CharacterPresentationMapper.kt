package com.blasck.reino.presentation.mapper

import com.blasck.reino.domain.model.Character
import com.blasck.reino.domain.model.CharacterAttributes
import com.blasck.reino.domain.model.CharacterImportIssue
import com.blasck.reino.domain.model.StoredCharacter
import com.blasck.reino.presentation.screen.model.CharacterModel
import com.blasck.reino.presentation.utils.formatNumber

fun StoredCharacter.toPresentationModel(): CharacterModel {
    val domain = character
    val attributes = domain.attributes

    return CharacterModel(
        information =
            CharacterModel.CharacterInformation(
                name = domain.identity.name,
                player = domain.identity.player,
                creationDate = domain.identity.creationDate,
                height = domain.identity.height,
                weight = domain.identity.weight,
                race = domain.identity.race,
                kingdom = domain.identity.kingdom,
                age = domain.identity.age,
                pointToSpend = domain.identity.pointsToSpend.formatNumber(),
                appearance = domain.identity.appearance,
            ),
        image = domain.image,
        importIssues =
            domain.importIssues.map {
                CharacterModel.ImportIssue(
                    fieldName = it.fieldName,
                    cellAddress = it.cellAddress,
                    rawValue = it.rawValue,
                )
            },
        status = attributes.toPresentationStatus(session, domain.importIssues),
        raceAndAdvantages =
            CharacterModel.RaceAndAdvantages(
                list =
                    domain.raceAndAdvantages.list.map { advantage ->
                        CharacterModel.RaceAndAdvantages.AdvantageModel(
                            name = advantage.name,
                            description = advantage.description,
                            cost = advantage.cost,
                            points = advantage.points,
                            chance = advantage.chance,
                            multiplier = advantage.multiplier,
                            multiplierType = advantage.multiplierType,
                        )
                    },
                totalCost = domain.raceAndAdvantages.totalCost,
            ),
        reactionModifiers =
            CharacterModel.ReactionModifiers(
                appearance = domain.reactionModifiers.appearance.value,
                status = domain.reactionModifiers.status.value,
                reputation = domain.reactionModifiers.reputation.value,
                additional =
                    domain.reactionModifiers.additional.map { modifier ->
                        CharacterModel.ReactionModifiers.AdditionalModifier(
                            name = modifier.name,
                            type = modifier.value,
                            cost = modifier.cost,
                        )
                    },
                totalCost = domain.reactionModifiers.totalCost,
            ),
        disadvantagesAndPeculiarities =
            CharacterModel.DisadvantagesAndPeculiarities(
                disadvantages =
                    domain.disadvantagesAndPeculiarities.disadvantages.map { item ->
                        CharacterModel.DisadvantagesAndPeculiarities.DPModel(
                            cost = item.cost,
                            name = item.name,
                            description = item.description,
                        )
                    },
                peculiarities =
                    domain.disadvantagesAndPeculiarities.peculiarities.map { item ->
                        CharacterModel.DisadvantagesAndPeculiarities.DPModel(
                            cost = item.cost,
                            name = item.name,
                            description = item.description,
                        )
                    },
                totalCost = domain.disadvantagesAndPeculiarities.totalCost,
            ),
        expertise =
            CharacterModel.Expertise(
                list =
                    domain.expertise.list.map { expertise ->
                        CharacterModel.Expertise.ExpertiseModel(
                            name = expertise.name,
                            description = expertise.description,
                            difficultType = expertise.difficultType,
                            difficultLevel = expertise.difficultLevel,
                            difficultExtra = expertise.difficultExtra,
                            cost = expertise.cost,
                            nh = expertise.nh,
                        )
                    },
                totalCost = domain.expertise.totalCost,
            ),
        rangedWeapon =
            CharacterModel.RangedWeapon(
                list =
                    domain.rangedWeapons.map { weapon ->
                        CharacterModel.RangedWeapon.RangedWeaponModel(
                            name = weapon.name,
                            damage = weapon.damage,
                            precision = weapon.precision,
                            halfDistance = weapon.halfDistance,
                            maxDistance = weapon.maxDistance,
                            fireRate = weapon.fireRate,
                            cdt = weapon.cdt,
                            tr = weapon.tr,
                            recoil = weapon.recoil,
                            st = weapon.st,
                            notes = weapon.notes,
                        )
                    },
            ),
        meleeWeapon =
            CharacterModel.MeleeWeapon(
                list =
                    domain.meleeWeapons.map { weapon ->
                        CharacterModel.MeleeWeapon.MeleeWeaponModel(
                            name = weapon.name,
                            quality = weapon.quality,
                            gdp = weapon.gdp,
                            gdpType = weapon.gdpType,
                            bal = weapon.bal,
                            balType = weapon.balType,
                            notes = weapon.notes,
                        )
                    },
            ),
        inventory =
            CharacterModel.Inventory(
                list =
                    domain.inventory.list.map { item ->
                        CharacterModel.Inventory.InventoryModel(
                            name = item.name,
                            quantity = item.quantity,
                            value = item.value,
                            weight = item.weight,
                            description = item.description,
                        )
                    },
            ),
        magicItems =
            CharacterModel.MagicItems(
                list =
                    domain.magicItems.list.map { item ->
                        CharacterModel.MagicItems.MagicItemModel(
                            name = item.name,
                            weight = item.weight,
                            cost = item.cost,
                            mana = item.mana,
                            description = item.description,
                        )
                    },
            ),
        armorList =
            CharacterModel.ArmorList(
                list =
                    domain.armorList.list.map { armor ->
                        CharacterModel.ArmorList.ArmorModel(
                            name = armor.name,
                            weight = armor.weight,
                            cost = armor.cost,
                            description = armor.description,
                            defenseRD = armor.defenseRD,
                            defenseDP = armor.defenseDP,
                        )
                    },
            ),
        money =
            CharacterModel.Money(
                gold = domain.money.gold.quantity,
                goldValue = domain.money.gold.value,
                silver = domain.money.silver.quantity,
                silverValue = domain.money.silver.value,
                copper = domain.money.copper.quantity,
                copperValue = domain.money.copper.value,
            ),
        annotations =
            CharacterModel.Annotations(
                list =
                    buildList {
                        addAll(domain.annotations)
                        if (session.notes.isNotBlank()) add(session.notes)
                    },
            ),
        pointResume =
            CharacterModel.PointResume(
                status = domain.pointResume.status,
                advantages = domain.pointResume.advantages,
                disadvantages = domain.pointResume.disadvantages,
                race = domain.pointResume.race,
                peculiarities = domain.pointResume.peculiarities,
                expertise = domain.pointResume.expertise,
                total = domain.pointResume.total,
            ),
        spellbook =
            CharacterModel.Spellbook(
                list =
                    domain.spellbook.list.map {
                        CharacterModel.Spellbook.SpellModel(
                            name = it.name,
                            level = it.level,
                            difficulty = it.difficulty,
                            cost = it.cost,
                            page = it.page,
                            spellClass = it.spellClass,
                            duration = it.duration,
                            castingCost = it.castingCost,
                            maintenanceCost = it.maintenanceCost,
                            castingTime = it.castingTime,
                            notes = it.notes,
                        )
                    },
            ),
    )
}

private fun CharacterAttributes.toPresentationStatus(
    session: com.blasck.reino.domain.model.CharacterSession,
    importIssues: List<CharacterImportIssue>,
): CharacterModel.StatusInformation {
    val defensesByPart = defenses.associateBy { it.part.lowercase() }
    val issueCells = importIssues.map { it.cellAddress }.toSet()
    fun issueAware(address: String, value: String) =
        if (address in issueCells) CharacterImportIssue.ERROR_VALUE else value

    fun defense(vararg names: String): CharacterModel.StatusInformation.Defense {
        val value =
            names.firstNotNullOfOrNull { name ->
                defensesByPart[name.lowercase()]
            }
        return value?.let {
            CharacterModel.StatusInformation.Defense(
                part = it.part,
                naturalDP = it.naturalDP,
                armorDP = it.armorDP,
                totalDP = it.totalDP,
                naturalRD = it.naturalRD,
                armorRD = it.armorRD,
                totalRD = it.totalRD,
            )
        } ?: CharacterModel.StatusInformation.Defense()
    }

    return CharacterModel.StatusInformation(
        strength = strength.toString(),
        strengthCost = strengthCost.toString(),
        strengthNextLevelCost = strengthNextLevelCost.toString(),
        dexterity = dexterity.toString(),
        dexterityCost = dexterityCost.toString(),
        dexterityNextLevelCost = dexterityNextLevelCost.toString(),
        constitution = health.toString(),
        constitutionCost = healthCost.toString(),
        constitutionNextLevelCost = healthNextLevelCost.toString(),
        intelligence = intelligence.toString(),
        intelligenceCost = intelligenceCost.toString(),
        intelligenceNextLevelCost = intelligenceNextLevelCost.toString(),
        fatigue = session.currentFatiguePoints.toString(),
        maxFatigue = maxFatiguePoints.toString(),
        mana = session.currentManaPoints.toString(),
        maxMana = maxManaPoints.toString(),
        will = will.toString(),
        perception = perception.toString(),
        hitPoints = session.currentHitPoints.toString(),
        maxHitPoints = maxHitPoints.toString(),
        baseWeight = basicLift.formatNumber(),
        damageGDP = damageGDP,
        damageBAL = damageBAL,
        basicDislocation = basicMove.toString(),
        basicSpeed = basicSpeed.formatNumber(),
        weightLevels =
            weightLevels.map {
                CharacterModel.StatusInformation.WeightStatus(
                    weightLevel = it.weightLevel,
                    weight = it.weight,
                    dislocation = it.dislocation,
                    dodge = it.dodge,
                )
            },
        dodgeBAL = issueAware("E63", dodgeBAL.toString()),
        dodgeGDP = issueAware("J63", dodgeGDP.toString()),
        parry =
            parry.map {
                CharacterModel.StatusInformation.Parry(
                    name = it.name,
                    value = it.value,
                )
            },
        block = block.toString(),
        headDefense = defense("Cabeça", "Cabeca"),
        bodyDefense = defense("Tronco"),
        legsDefense = defense("Pernas"),
        armDefense = defense("Braços", "Bracos"),
        handsDefense = defense("Mãos", "Maos"),
        feetDefense = defense("Pés", "Pes"),
        totalCost = totalCost.toString(),
    )
}
