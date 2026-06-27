package com.blasck.reino.domain.update

import com.blasck.reino.domain.model.ArmorList
import com.blasck.reino.domain.model.Character
import com.blasck.reino.domain.model.CharacterAttributes
import com.blasck.reino.domain.model.DisadvantagesAndPeculiarities
import com.blasck.reino.domain.model.Expertise
import com.blasck.reino.domain.model.Inventory
import com.blasck.reino.domain.model.MagicItems
import com.blasck.reino.domain.model.MeleeWeapon
import com.blasck.reino.domain.model.RaceAndAdvantages
import com.blasck.reino.domain.model.RangedWeapon
import com.blasck.reino.domain.model.Spellbook
import java.text.Normalizer

data class CharacterUpdatePreview(
    val changes: List<CharacterChange>,
) {
    val hasChanges: Boolean = changes.isNotEmpty()
    val addedCount: Int = changes.count { it.type == CharacterChangeType.ADDED }
    val removedCount: Int = changes.count { it.type == CharacterChangeType.REMOVED }
    val changedCount: Int = changes.count { it.type == CharacterChangeType.CHANGED }
}

data class CharacterChange(
    val category: CharacterChangeCategory,
    val label: String,
    val type: CharacterChangeType,
    val currentValue: String,
    val newValue: String,
)

enum class CharacterChangeCategory(val label: String) {
    IDENTITY("Identidade"),
    ATTRIBUTES("Atributos"),
    DEFENSES("Defesas"),
    SKILLS("Pericias"),
    ADVANTAGES("Vantagens"),
    DISADVANTAGES("Desvantagens e peculiaridades"),
    WEAPONS("Armas"),
    EQUIPMENT("Equipamentos"),
    ARMOR("Armaduras"),
    MAGIC_ITEMS("Itens magicos"),
    SPELLS("Magias"),
    NOTES("Anotacoes"),
}

enum class CharacterChangeType(val label: String) {
    ADDED("Adicionado"),
    REMOVED("Removido"),
    CHANGED("Alterado"),
}

fun Character.previewUpdateWith(newCharacter: Character): CharacterUpdatePreview =
    CharacterUpdatePreview(
        changes =
            buildList {
                compareScalar(CharacterChangeCategory.IDENTITY, "Nome", identity.name, newCharacter.identity.name)
                compareScalar(CharacterChangeCategory.IDENTITY, "Jogador", identity.player, newCharacter.identity.player)
                compareScalar(CharacterChangeCategory.IDENTITY, "Raca", identity.race, newCharacter.identity.race)
                compareScalar(CharacterChangeCategory.IDENTITY, "Reino", identity.kingdom, newCharacter.identity.kingdom)
                compareScalar(CharacterChangeCategory.IDENTITY, "Idade", identity.age, newCharacter.identity.age)
                compareScalar(CharacterChangeCategory.IDENTITY, "Altura", identity.height, newCharacter.identity.height)
                compareScalar(CharacterChangeCategory.IDENTITY, "Peso", identity.weight, newCharacter.identity.weight)
                compareScalar(
                    CharacterChangeCategory.IDENTITY,
                    "Pontos a gastar",
                    identity.pointsToSpend.toString(),
                    newCharacter.identity.pointsToSpend.toString(),
                )
                compareAttributes(attributes, newCharacter.attributes)
                compareDefenses(attributes.defenses, newCharacter.attributes.defenses)
                compareList(
                    CharacterChangeCategory.SKILLS,
                    expertise.list,
                    newCharacter.expertise.list,
                    key = { it.name },
                    label = { it.name },
                    value = Expertise.ExpertiseModel::summary,
                )
                compareList(
                    CharacterChangeCategory.ADVANTAGES,
                    raceAndAdvantages.list,
                    newCharacter.raceAndAdvantages.list,
                    key = { it.name },
                    label = { it.name },
                    value = RaceAndAdvantages.AdvantageModel::summary,
                )
                compareList(
                    CharacterChangeCategory.DISADVANTAGES,
                    disadvantagesAndPeculiarities.disadvantages + disadvantagesAndPeculiarities.peculiarities,
                    newCharacter.disadvantagesAndPeculiarities.disadvantages +
                        newCharacter.disadvantagesAndPeculiarities.peculiarities,
                    key = { it.name },
                    label = { it.name },
                    value = DisadvantagesAndPeculiarities.DPModel::summary,
                )
                compareList(
                    CharacterChangeCategory.WEAPONS,
                    meleeWeapons,
                    newCharacter.meleeWeapons,
                    key = { "melee-${it.name}" },
                    label = { it.name },
                    value = MeleeWeapon::summary,
                )
                compareList(
                    CharacterChangeCategory.WEAPONS,
                    rangedWeapons,
                    newCharacter.rangedWeapons,
                    key = { "ranged-${it.name}" },
                    label = { it.name },
                    value = RangedWeapon::summary,
                )
                compareList(
                    CharacterChangeCategory.EQUIPMENT,
                    inventory.list,
                    newCharacter.inventory.list,
                    key = { it.name },
                    label = { it.name },
                    value = Inventory.InventoryModel::summary,
                )
                compareList(
                    CharacterChangeCategory.ARMOR,
                    armorList.list,
                    newCharacter.armorList.list,
                    key = { it.name },
                    label = { it.name },
                    value = ArmorList.ArmorModel::summary,
                )
                compareList(
                    CharacterChangeCategory.MAGIC_ITEMS,
                    magicItems.list,
                    newCharacter.magicItems.list,
                    key = { it.name },
                    label = { it.name },
                    value = MagicItems.MagicItemModel::summary,
                )
                compareList(
                    CharacterChangeCategory.SPELLS,
                    spellbook.list,
                    newCharacter.spellbook.list,
                    key = { it.name },
                    label = { it.name },
                    value = Spellbook.SpellModel::summary,
                )
                compareList(
                    CharacterChangeCategory.NOTES,
                    annotations,
                    newCharacter.annotations,
                    key = { it.take(40) },
                    label = { it.take(40).ifBlank { "Anotacao" } },
                    value = { it },
                )
            },
    )

private fun MutableList<CharacterChange>.compareAttributes(
    current: CharacterAttributes,
    new: CharacterAttributes,
) {
    compareScalar(CharacterChangeCategory.ATTRIBUTES, "ST", current.strength.toString(), new.strength.toString())
    compareScalar(CharacterChangeCategory.ATTRIBUTES, "DX", current.dexterity.toString(), new.dexterity.toString())
    compareScalar(CharacterChangeCategory.ATTRIBUTES, "IQ", current.intelligence.toString(), new.intelligence.toString())
    compareScalar(CharacterChangeCategory.ATTRIBUTES, "HT", current.health.toString(), new.health.toString())
    compareScalar(CharacterChangeCategory.ATTRIBUTES, "Vida maxima", current.hitPoints.toString(), new.hitPoints.toString())
    compareScalar(CharacterChangeCategory.ATTRIBUTES, "Fadiga maxima", current.fatiguePoints.toString(), new.fatiguePoints.toString())
    compareScalar(CharacterChangeCategory.ATTRIBUTES, "Mana maxima", current.manaPoints.toString(), new.manaPoints.toString())
    compareScalar(CharacterChangeCategory.ATTRIBUTES, "Vontade", current.will.toString(), new.will.toString())
    compareScalar(CharacterChangeCategory.ATTRIBUTES, "Percepcao", current.perception.toString(), new.perception.toString())
    compareScalar(CharacterChangeCategory.ATTRIBUTES, "Velocidade", current.basicSpeed.toString(), new.basicSpeed.toString())
    compareScalar(CharacterChangeCategory.ATTRIBUTES, "Deslocamento", current.basicMove.toString(), new.basicMove.toString())
    compareScalar(CharacterChangeCategory.ATTRIBUTES, "Esquiva", current.dodge.toString(), new.dodge.toString())
    compareScalar(CharacterChangeCategory.ATTRIBUTES, "Bloqueio", current.block.toString(), new.block.toString())
}

private fun MutableList<CharacterChange>.compareDefenses(
    current: List<CharacterAttributes.Defense>,
    new: List<CharacterAttributes.Defense>,
) {
    compareList(
        CharacterChangeCategory.DEFENSES,
        current,
        new,
        key = { it.part },
        label = { it.part },
        value = { "DP ${it.totalDP} / RD ${it.totalRD}" },
    )
}

private fun MutableList<CharacterChange>.compareScalar(
    category: CharacterChangeCategory,
    label: String,
    current: String,
    new: String,
) {
    if (current.normalizedValue() != new.normalizedValue()) {
        add(
            CharacterChange(
                category = category,
                label = label,
                type = CharacterChangeType.CHANGED,
                currentValue = current,
                newValue = new,
            ),
        )
    }
}

private fun <T> MutableList<CharacterChange>.compareList(
    category: CharacterChangeCategory,
    current: List<T>,
    new: List<T>,
    key: (T) -> String,
    label: (T) -> String,
    value: (T) -> String,
) {
    val currentByKey = current.associateBy { key(it).stableKey() }
    val newByKey = new.associateBy { key(it).stableKey() }
    val keys = (currentByKey.keys + newByKey.keys).sorted()

    keys.forEach { itemKey ->
        val currentItem = currentByKey[itemKey]
        val newItem = newByKey[itemKey]
        when {
            currentItem == null && newItem != null ->
                add(
                    CharacterChange(
                        category = category,
                        label = label(newItem),
                        type = CharacterChangeType.ADDED,
                        currentValue = "",
                        newValue = value(newItem),
                    ),
                )

            currentItem != null && newItem == null ->
                add(
                    CharacterChange(
                        category = category,
                        label = label(currentItem),
                        type = CharacterChangeType.REMOVED,
                        currentValue = value(currentItem),
                        newValue = "",
                    ),
                )

            currentItem != null && newItem != null &&
                value(currentItem).normalizedValue() != value(newItem).normalizedValue() ->
                add(
                    CharacterChange(
                        category = category,
                        label = label(newItem).ifBlank { label(currentItem) },
                        type = CharacterChangeType.CHANGED,
                        currentValue = value(currentItem),
                        newValue = value(newItem),
                    ),
                )
        }
    }
}

private fun Expertise.ExpertiseModel.summary(): String =
    listOf(name, "NH $nh", difficultType, difficultLevel, difficultExtra, "Custo $cost", description)
        .filter(String::isNotBlank)
        .joinToString(" | ")

private fun RaceAndAdvantages.AdvantageModel.summary(): String =
    listOf(name, cost, points, chance, multiplier, multiplierType, description)
        .filter(String::isNotBlank)
        .joinToString(" | ")

private fun DisadvantagesAndPeculiarities.DPModel.summary(): String =
    listOf(name, cost, description).filter(String::isNotBlank).joinToString(" | ")

private fun MeleeWeapon.summary(): String =
    listOf(name, quality, gdp, gdpType, bal, balType, notes).filter(String::isNotBlank).joinToString(" | ")

private fun RangedWeapon.summary(): String =
    listOf(name, damage, precision, halfDistance, maxDistance, fireRate, cdt, tr, recoil, st, notes)
        .filter(String::isNotBlank)
        .joinToString(" | ")

private fun Inventory.InventoryModel.summary(): String =
    listOf(name, "Qtd $quantity", "Valor $value", "Peso $weight", description)
        .filter(String::isNotBlank)
        .joinToString(" | ")

private fun ArmorList.ArmorModel.summary(): String =
    listOf(name, "RD $defenseRD", "DP $defenseDP", "Peso $weight", "Custo $cost", description)
        .filter(String::isNotBlank)
        .joinToString(" | ")

private fun MagicItems.MagicItemModel.summary(): String =
    listOf(name, "Mana $mana", "Peso $weight", "Custo $cost", description)
        .filter(String::isNotBlank)
        .joinToString(" | ")

private fun Spellbook.SpellModel.summary(): String =
    listOf(name, "NH $level", difficulty, "Custo $cost", page, spellClass, duration, notes)
        .filter(String::isNotBlank)
        .joinToString(" | ")

private fun String.stableKey(): String = normalizedValue().ifBlank { "sem-chave" }

private fun String.normalizedValue(): String =
    Normalizer.normalize(trim().lowercase(), Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
        .replace(Regex("\\s+"), " ")
