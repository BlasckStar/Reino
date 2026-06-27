package com.blasck.reino.domain.model

/**
 * Dados permanentes do personagem.
 *
 * Este modelo pertence ao domínio e não depende de componentes do Compose,
 * células da planilha ou do formato usado para persistência.
 */
data class Character(
    val identity: CharacterIdentity = CharacterIdentity(),
    val attributes: CharacterAttributes = CharacterAttributes(),
    val importIssues: List<CharacterImportIssue> = emptyList(),

    val image: String = "",

    val raceAndAdvantages: RaceAndAdvantages = RaceAndAdvantages(),
    val reactionModifiers: ReactionModifiers = ReactionModifiers(),
    val disadvantagesAndPeculiarities: DisadvantagesAndPeculiarities = DisadvantagesAndPeculiarities(),

    val expertise: Expertise = Expertise(),

    val rangedWeapons: List<RangedWeapon> = emptyList(),
    val meleeWeapons: List<MeleeWeapon> = emptyList(),

    val inventory: Inventory = Inventory(),
    val magicItems: MagicItems = MagicItems(),
    val armorList: ArmorList = ArmorList(),

    val money: Money = Money(),
    val annotations: List<String> = emptyList(),
    val pointResume: PointResume = PointResume(),
    val spellbook: Spellbook = Spellbook(),
)

data class CharacterImportIssue(
    val fieldName: String = "",
    val cellAddress: String = "",
    val rawValue: String = "",
) {
    companion object {
        const val ERROR_VALUE = "ERRO_REF"
    }
}

data class CharacterIdentity(
    val name: String = "",
    val player: String = "",
    val creationDate: String = "",
    val height: String = "",
    val weight: String = "",
    val race: String = "",
    val kingdom: String = "",
    val age: String = "",
    val pointsToSpend: Double = 0.0,
    val appearance: String = "",
)

data class CharacterAttributes(
    val strength: Int = 0,
    val strengthCost: Int = 0,
    val strengthNextLevelCost: Int = 0,

    val dexterity: Int = 0,
    val dexterityCost: Int = 0,
    val dexterityNextLevelCost: Int = 0,

    val intelligence: Int = 0,
    val intelligenceCost: Int = 0,
    val intelligenceNextLevelCost: Int = 0,

    val health: Int = 0,
    val healthCost: Int = 0,
    val healthNextLevelCost: Int = 0,

    val fatiguePoints: Int = 0,
    val maxFatiguePoints: Int = 0,

    val manaPoints: Int = 0,
    val maxManaPoints: Int = 0,

    val will: Int = 0,
    val perception: Int = 0,

    val hitPoints: Int = 0,
    val maxHitPoints: Int = 0,

    val basicLift: Double = 0.0,

    val damageGDP: String = "",
    val damageBAL: String = "",

    val basicSpeed: Double = 0.0,
    val basicMove: Int = 0,

    /**
     * Mantido para compatibilidade com o código atual.
     * Pode representar a esquiva principal da ficha.
     */
    val dodge: Int = 0,

    val dodgeBAL: Int = 0,
    val dodgeGDP: Int = 0,

    val weightLevels: List<WeightStatus> = emptyList(),
    val parry: List<Parry> = emptyList(),
    val block: Int = 0,

    val defenses: List<Defense> = emptyList(),

    val totalCost: Int = 0,
) {
    data class WeightStatus(
        val weightLevel: String = "",
        val weight: String = "",
        val dislocation: String = "",
        val dodge: String = "",
    )

    data class Parry(
        val name: String = "",
        val value: String = "",
    )

    data class Defense(
        val part: String = "",
        val naturalDP: String = "",
        val armorDP: String = "",
        val totalDP: String = "",
        val naturalRD: String = "",
        val armorRD: String = "",
        val totalRD: String = "",
    )
}

data class RaceAndAdvantages(
    val list: List<AdvantageModel> = emptyList(),
    val totalCost: String = "",
) {
    data class AdvantageModel(
        val code: String = "",
        val name: String = "",
        val description: String = "",
        val cost: String = "",
        val points: String = "",
        val chance: String = "",
        val multiplier: String = "",
        val multiplierType: String = "",
        val extraValue: String = "",
    )
}

data class ReactionModifiers(
    val appearance: Modifier = Modifier(),
    val status: Modifier = Modifier(),
    val reputation: Modifier = Modifier(),
    val additional: List<Modifier> = emptyList(),
    val totalCost: String = "",
) {
    data class Modifier(
        val name: String = "",
        val value: String = "",
        val cost: String = "",
    )
}

data class DisadvantagesAndPeculiarities(
    val disadvantages: List<DPModel> = emptyList(),
    val peculiarities: List<DPModel> = emptyList(),
    val disadvantagesTotalCost: String = "",
    val peculiaritiesTotalCost: String = "",
    val totalCost: String = "",
) {
    data class DPModel(
        val cost: String = "",
        val name: String = "",
        val description: String = "",
    )
}

data class Expertise(
    val list: List<ExpertiseModel> = emptyList(),
    val totalCost: String = "",
) {
    data class ExpertiseModel(
        val name: String = "",
        val description: String = "",
        val difficultType: String = "",
        val difficultLevel: String = "",
        val difficultExtra: String = "",
        val cost: String = "",
        val nh: String = "",
        val modifiers: List<String> = emptyList(),
    )
}

data class RangedWeapon(
    val name: String = "",
    val damage: String = "",
    val precision: String = "",
    val halfDistance: String = "",
    val maxDistance: String = "",
    val fireRate: String = "",
    val cdt: String = "",
    val tr: String = "",
    val recoil: String = "",
    val st: String = "",
    val notes: String = "",
)

data class MeleeWeapon(
    val name: String = "",
    val quality: String = "",
    val gdp: String = "",
    val gdpType: String = "",
    val bal: String = "",
    val balType: String = "",
    val notes: String = "",
)

data class Inventory(
    val list: List<InventoryModel> = emptyList(),
    val totalWeight: String = "",
    val totalCost: String = "",
) {
    data class InventoryModel(
        val name: String = "",
        val quantity: String = "",
        val value: String = "",
        val weight: String = "",
        val description: String = "",
    )
}

data class MagicItems(
    val list: List<MagicItemModel> = emptyList(),
) {
    data class MagicItemModel(
        val name: String = "",
        val weight: String = "",
        val cost: String = "",
        val mana: String = "",
        val description: String = "",
    )
}

data class ArmorList(
    val list: List<ArmorModel> = emptyList(),
    val totalWeight: String = "",
    val totalCost: String = "",
) {
    data class ArmorModel(
        val name: String = "",
        val weight: String = "",
        val cost: String = "",
        val description: String = "",
        val defenseRD: String = "",
        val defenseDP: String = "",
    )
}

data class Money(
    val gold: Coin = Coin(),
    val silver: Coin = Coin(),
    val copper: Coin = Coin(),
) {
    data class Coin(
        val quantity: String = "",
        val value: String = "",
    )
}

data class PointResume(
    val status: String = "",
    val advantages: String = "",
    val disadvantages: String = "",
    val race: String = "",
    val peculiarities: String = "",
    val expertise: String = "",
    val total: String = "",
)

data class Spellbook(
    val list: List<SpellModel> = emptyList(),
) {
    data class SpellModel(
        val cost: String = "",
        val name: String = "",
        val difficulty: String = "",
        val level: String = "",
        val page: String = "",
        val spellClass: String = "",
        val duration: String = "",
        val castingCost: String = "",
        val maintenanceCost: String = "",
        val castingTime: String = "",
        val notes: String = "",
    )
}
