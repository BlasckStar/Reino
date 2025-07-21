package com.blasck.reino.presentation.screen.model

import com.blasck.reino.presentation.utils.Constants

data class CharacterModel(
    val information: CharacterInformation = CharacterInformation(),
    val image: String = Constants.EMPTY_STRING,
    val status: StatusInformation = StatusInformation(),
    val raceAndAdvantages: RaceAndAdvantages = RaceAndAdvantages(),
    val reactionModifiers: ReactionModifiers = ReactionModifiers(),
    val disadvantagesAndPeculiarities: DisadvantagesAndPeculiarities = DisadvantagesAndPeculiarities(),
    val expertise: Expertise = Expertise(),
    val rangedWeapon: RangedWeapon = RangedWeapon(),
    val meleeWeapon: MeleeWeapon = MeleeWeapon(),
    val inventory: Inventory = Inventory(),
    val magicItems: MagicItems = MagicItems(),
    val armorList: ArmorList = ArmorList(),
    val money: Money = Money(),
    val annotations: Annotations = Annotations(),
    val pointResume: PointResume = PointResume()
){
    data class CharacterInformation(
        val name: String = Constants.EMPTY_STRING,
        val player: String = Constants.EMPTY_STRING,
        val creationDate: String = Constants.EMPTY_STRING,
        val height: String = Constants.EMPTY_STRING,
        val weight: String = Constants.EMPTY_STRING,
        val race: String = Constants.EMPTY_STRING,
        val kingdom: String = Constants.EMPTY_STRING,
        val age: String = Constants.EMPTY_STRING,
        val pointToSpend: String = Constants.EMPTY_STRING,
        val appearance: String = Constants.EMPTY_STRING,
    )

    data class StatusInformation(
        val strength: String = Constants.EMPTY_STRING,
        val strengthCost: String = Constants.EMPTY_STRING,
        val strengthNextLevelCost: String = Constants.EMPTY_STRING,
        val dexterity: String = Constants.EMPTY_STRING,
        val dexterityCost: String = Constants.EMPTY_STRING,
        val dexterityNextLevelCost: String = Constants.EMPTY_STRING,
        val constitution: String = Constants.EMPTY_STRING,
        val constitutionCost: String = Constants.EMPTY_STRING,
        val constitutionNextLevelCost: String = Constants.EMPTY_STRING,
        val intelligence: String = Constants.EMPTY_STRING,
        val intelligenceCost: String = Constants.EMPTY_STRING,
        val intelligenceNextLevelCost: String = Constants.EMPTY_STRING,
        val fatigue: String = Constants.EMPTY_STRING,
        val mana: String = Constants.EMPTY_STRING,
        val will: String = Constants.EMPTY_STRING,
        val perception: String = Constants.EMPTY_STRING,
        val hitPoints: String = Constants.EMPTY_STRING,
        val baseWeight: String = Constants.EMPTY_STRING,
        val damageGDP: String = Constants.EMPTY_STRING,
        val damageBAL: String = Constants.EMPTY_STRING,
        val basicDislocation: String = Constants.EMPTY_STRING,
        val basicSpeed: String = Constants.EMPTY_STRING,
        val weightLevels: List<WeightStatus> = listOf(),
        val dodgeBAL: String = Constants.EMPTY_STRING,
        val dodgeGDP: String = Constants.EMPTY_STRING,
        val parry: List<Parry> = listOf(),
        val block: String = Constants.EMPTY_STRING,
        val headDefense: Defense = Defense(),
        val bodyDefense: Defense = Defense(),
        val legsDefense: Defense = Defense(),
        val armDefense: Defense = Defense(),
        val handsDefense: Defense = Defense(),
        val feetDefense: Defense = Defense(),
        val totalCost: String = Constants.EMPTY_STRING
        )
    {
        data class WeightStatus(
            val weightLevel: String = Constants.EMPTY_STRING,
            val weight: String = Constants.EMPTY_STRING,
            val dislocation: String = Constants.EMPTY_STRING,
            val dodge: String = Constants.EMPTY_STRING,
        )

        data class Parry(
            val name: String = Constants.EMPTY_STRING,
            val value: String = Constants.EMPTY_STRING,
        )

        data class Defense(
            val naturalDP: String = Constants.EMPTY_STRING,
            val armorDP: String = Constants.EMPTY_STRING,
            val naturalRD: String = Constants.EMPTY_STRING,
            val armorRD: String = Constants.EMPTY_STRING,
        )
    }

    data class RaceAndAdvantages(
        val list: List<AdvantageModel> = listOf(),
        val totalCost: String = Constants.EMPTY_STRING
    ){
        data class AdvantageModel(
            val name: String = Constants.EMPTY_STRING,
            val description: String = Constants.EMPTY_STRING,
            val cost: String = Constants.EMPTY_STRING,
            val points: String = Constants.EMPTY_STRING,
            val chance: String = Constants.EMPTY_STRING,
            val multiplier: String = Constants.EMPTY_STRING,
            val multiplierType: String = Constants.EMPTY_STRING,
        )
    }

    data class ReactionModifiers(
        val appearance: String = Constants.EMPTY_STRING,
        val status: String = Constants.EMPTY_STRING,
        val reputation: String = Constants.EMPTY_STRING,
        val additional: List<AdditionalModifier> = listOf(),
        val totalCost: String = Constants.EMPTY_STRING,
    ){
        data class AdditionalModifier(
            val name: String = Constants.EMPTY_STRING,
            val type: String = Constants.EMPTY_STRING,
            val cost: String = Constants.EMPTY_STRING,
        )
    }

    data class DisadvantagesAndPeculiarities(
        val list: List<DPModel> = listOf(),
        val totalCost: String = Constants.EMPTY_STRING
    ){
        data class DPModel(
            val cost: String = Constants.EMPTY_STRING,
            val name: String = Constants.EMPTY_STRING,
            val description: String = Constants.EMPTY_STRING,
        )
    }

    data class Expertise(
        val list : List<ExpertiseModel> = listOf(),
        val totalCost: String = Constants.EMPTY_STRING
    ){
        data class ExpertiseModel(
            val name: String = Constants.EMPTY_STRING,
            val description: String = Constants.EMPTY_STRING,
            val difficultType: String = Constants.EMPTY_STRING,
            val difficultLevel: String = Constants.EMPTY_STRING,
            val cost: String = Constants.EMPTY_STRING,
            val nh: String = Constants.EMPTY_STRING,
        )
    }

    data class RangedWeapon(
        val list: List<RangedWeaponModel> = listOf()
    ){
        data class RangedWeaponModel(
            val name: String  = Constants.EMPTY_STRING,
            val damage: String = Constants.EMPTY_STRING,
            val precision: String = Constants.EMPTY_STRING,
            val halfDistance: String = Constants.EMPTY_STRING,
            val maxDistance: String = Constants.EMPTY_STRING,
            val fireRate: String = Constants.EMPTY_STRING,
            val cdt: String = Constants.EMPTY_STRING,
            val tr: String = Constants.EMPTY_STRING,
            val recoil: String = Constants.EMPTY_STRING,
            val st: String = Constants.EMPTY_STRING,
            val notes: String = Constants.EMPTY_STRING,
        )
    }

    data class MeleeWeapon(
        val list: List<MeleeWeaponModel> = listOf()
    ){
        data class MeleeWeaponModel(
            val name: String = Constants.EMPTY_STRING,
            val quality: String = Constants.EMPTY_STRING,
            val gdp: String = Constants.EMPTY_STRING,
            val gdpType: String = Constants.EMPTY_STRING,
            val bal: String = Constants.EMPTY_STRING,
            val balType: String = Constants.EMPTY_STRING,
            val notes: String = Constants.EMPTY_STRING,
        )
    }

    data class Inventory(
        val list: List<InventoryModel> = listOf()
    ){
        data class InventoryModel(
            val name: String = Constants.EMPTY_STRING,
            val quantity: String = Constants.EMPTY_STRING,
            val value: String = Constants.EMPTY_STRING,
            val weight: String = Constants.EMPTY_STRING,
            val description: String = Constants.EMPTY_STRING,
        )
    }

    data class MagicItems(
        val list: List<MagicItemModel> = listOf()
    ){
        data class MagicItemModel(
            val name: String = Constants.EMPTY_STRING,
            val weight: String = Constants.EMPTY_STRING,
            val cost: String = Constants.EMPTY_STRING,
            val mana: String = Constants.EMPTY_STRING,
            val description: String = Constants.EMPTY_STRING,
        )
    }

    data class ArmorList(
        val list: List<ArmorModel> = listOf()
    ){
        data class ArmorModel(
            val name: String = Constants.EMPTY_STRING,
            val weight: String = Constants.EMPTY_STRING,
            val cost: String = Constants.EMPTY_STRING,
            val description: String = Constants.EMPTY_STRING,
            val defenseRD: String = Constants.EMPTY_STRING,
            val defenseDP: String = Constants.EMPTY_STRING,
        )
    }

    data class Money(
        val gold: String = Constants.EMPTY_STRING,
        val goldValue: String = Constants.EMPTY_STRING,
        val silver: String = Constants.EMPTY_STRING,
        val silverValue: String = Constants.EMPTY_STRING,
        val copper: String = Constants.EMPTY_STRING,
        val copperValue: String = Constants.EMPTY_STRING,
    )

    data class Annotations(
        val list: List<String> = listOf()
    )

    data class PointResume(
        val status: String = Constants.EMPTY_STRING,
        val advantages: String = Constants.EMPTY_STRING,
        val disadvantages: String = Constants.EMPTY_STRING,
        val race: String = Constants.EMPTY_STRING,
        val peculiarities: String = Constants.EMPTY_STRING,
        val expertise: String = Constants.EMPTY_STRING,
        val total: String = Constants.EMPTY_STRING
    )
}
