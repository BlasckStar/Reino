package com.blasck.reino.data.importer

import com.blasck.reino.domain.importer.CharacterSheetImportFailure
import com.blasck.reino.domain.importer.CharacterSheetImportResult
import com.blasck.reino.domain.importer.CharacterSheetImporter
import com.blasck.reino.domain.model.Character
import com.blasck.reino.domain.model.CharacterAttributes
import com.blasck.reino.domain.model.CharacterIdentity
import com.blasck.reino.domain.model.CharacterImportIssue
import com.blasck.reino.domain.model.Expertise
import com.blasck.reino.domain.model.ArmorList
import com.blasck.reino.domain.model.DisadvantagesAndPeculiarities
import com.blasck.reino.domain.model.Inventory
import com.blasck.reino.domain.model.MagicItems
import com.blasck.reino.domain.model.MeleeWeapon
import com.blasck.reino.domain.model.Money
import com.blasck.reino.domain.model.PointResume
import com.blasck.reino.domain.model.RaceAndAdvantages
import com.blasck.reino.domain.model.RangedWeapon
import com.blasck.reino.domain.model.ReactionModifiers
import com.blasck.reino.domain.model.Spellbook
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.math.BigDecimal
import java.util.zip.ZipInputStream
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Leitor mínimo de XLSX compatível com Android.
 *
 * Um XLSX é um arquivo ZIP contendo XMLs. Este importador lê apenas nomes de
 * abas, strings compartilhadas e valores armazenados nas células necessárias.
 */
class XlsxCharacterSheetImporter : CharacterSheetImporter {
    override fun import(input: InputStream): CharacterSheetImportResult {
        return try {
            val entries = readZipEntries(input)
            if (entries.isEmpty() || CONTENT_TYPES_PATH !in entries) {
                return CharacterSheetImportResult.Failure(
                    CharacterSheetImportFailure.InvalidFile,
                )
            }

            val sheets = readSheetPaths(entries)
            validateRequiredSheets(sheets.keys)?.let {
                return CharacterSheetImportResult.Failure(it)
            }

            val sharedStrings = readSharedStrings(entries[SHARED_STRINGS_PATH])
            val sheetPath = checkNotNull(sheets[ReinoSheetV1Fields.CHARACTER_SHEET])
            val sheetBytes =
                entries[sheetPath]
                    ?: return CharacterSheetImportResult.Failure(
                        CharacterSheetImportFailure.MissingSheet(
                            ReinoSheetV1Fields.CHARACTER_SHEET,
                        ),
                    )
            val values = readRequiredCells(sheetBytes, sharedStrings)
            val spellbookPath = checkNotNull(sheets[ReinoSheetV1Fields.SPELLBOOK_SHEET])
            val spellbookValues =
                entries[spellbookPath]?.let { readCells(it, sharedStrings, emptySet()) }
                    .orEmpty()
            createCharacter(values, spellbookValues)
        } catch (_: java.util.zip.ZipException) {
            CharacterSheetImportResult.Failure(CharacterSheetImportFailure.InvalidFile)
        } catch (error: Exception) {
            CharacterSheetImportResult.Failure(
                CharacterSheetImportFailure.ReadError(error.message),
            )
        }
    }

    private fun readZipEntries(input: InputStream): Map<String, ByteArray> {
        val entries = mutableMapOf<String, ByteArray>()
        ZipInputStream(input.buffered()).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                if (!entry.isDirectory) {
                    entries[entry.name] = zip.readBytes()
                }
                zip.closeEntry()
                entry = zip.nextEntry
            }
        }
        return entries
    }

    private fun readSheetPaths(entries: Map<String, ByteArray>): Map<String, String> {
        val workbook = parseXml(checkNotNull(entries[WORKBOOK_PATH]))
        val relationships = parseXml(checkNotNull(entries[WORKBOOK_RELS_PATH]))
        val relationshipTargets = mutableMapOf<String, String>()
        val relationshipNodes = relationships.getElementsByTagName("Relationship")

        for (index in 0 until relationshipNodes.length) {
            val relationship = relationshipNodes.item(index) as Element
            relationshipTargets[relationship.getAttribute("Id")] =
                normalizeWorkbookTarget(relationship.getAttribute("Target"))
        }

        val result = mutableMapOf<String, String>()
        val sheetNodes = workbook.getElementsByTagName("sheet")
        for (index in 0 until sheetNodes.length) {
            val sheet = sheetNodes.item(index) as Element
            val name = sheet.getAttribute("name")
            val relationshipId =
                sheet.getAttribute("r:id").ifBlank {
                    sheet.getAttributeNS(
                        "http://schemas.openxmlformats.org/officeDocument/2006/relationships",
                        "id",
                    )
                }
            relationshipTargets[relationshipId]?.let { result[name] = it }
        }
        return result
    }

    private fun normalizeWorkbookTarget(target: String): String {
        val normalized = target.replace('\\', '/').removePrefix("/")
        return if (normalized.startsWith("xl/")) normalized else "xl/$normalized"
    }

    private fun readSharedStrings(bytes: ByteArray?): List<String> {
        if (bytes == null) return emptyList()

        val document = parseXml(bytes)
        val items = document.getElementsByTagName("si")
        return buildList {
            for (index in 0 until items.length) {
                val item = items.item(index) as Element
                val textNodes = item.getElementsByTagName("t")
                add(
                    buildString {
                        for (textIndex in 0 until textNodes.length) {
                            append(textNodes.item(textIndex).textContent)
                        }
                    },
                )
            }
        }
    }

    private fun readRequiredCells(
        bytes: ByteArray,
        sharedStrings: List<String>,
    ): Map<String, String> = readCells(bytes, sharedStrings, emptySet())

    private fun readCells(
        bytes: ByteArray,
        sharedStrings: List<String>,
        addresses: Set<String>,
    ): Map<String, String> {
        val document = parseXml(bytes)
        val cells = document.getElementsByTagName("c")
        val values = mutableMapOf<String, String>()

        for (index in 0 until cells.length) {
            val cell = cells.item(index) as Element
            val address = cell.getAttribute("r")
            if (addresses.isNotEmpty() && address !in addresses) continue

            val type = cell.getAttribute("t")
            val rawValue = cell.firstChildText("v")
            val value =
                when (type) {
                    "s" -> rawValue?.toIntOrNull()?.let(sharedStrings::getOrNull).orEmpty()
                    "inlineStr" -> cell.firstChildText("t").orEmpty()
                    "b" -> if (rawValue == "1") "true" else "false"
                    else -> rawValue.orEmpty()
                }
            values[address] = value.trim()
        }
        return values
    }

    private fun createCharacter(
        values: Map<String, String>,
        spellbookValues: Map<String, String>,
    ): CharacterSheetImportResult {
        val importIssues = linkedMapOf<String, CharacterImportIssue>()

        fun registerRefIssue(
            fieldName: String,
            address: String,
            value: String,
        ) {
            importIssues[address] =
                CharacterImportIssue(
                    fieldName = fieldName,
                    cellAddress = address,
                    rawValue = value,
                )
        }

        values
            .filterValues { it.isBrokenReference() }
            .forEach { (address, value) ->
                registerRefIssue("Campo da ficha", address, value)
            }

        fun requiredText(
            fieldName: String,
            address: String,
        ): String {
            val value = values[address].orEmpty()
            if (value.isBlank()) {
                throw ImportFieldException(
                    CharacterSheetImportFailure.MissingRequiredField(fieldName, address),
                )
            }
            if (value.isBrokenReference()) {
                registerRefIssue(fieldName, address, value)
                return CharacterImportIssue.ERROR_VALUE
            }
            return value
        }

        fun optionalText(address: String): String = value(values, address)

        fun requiredInt(
            fieldName: String,
            address: String,
        ): Int {
            val value = requiredText(fieldName, address)
            if (value == CharacterImportIssue.ERROR_VALUE) return 0
            return value.toBigDecimalOrNull()?.toInt()
                ?: throw ImportFieldException(
                    CharacterSheetImportFailure.InvalidFieldValue(fieldName, address, value),
                )
        }

        fun requiredDouble(
            fieldName: String,
            address: String,
        ): Double {
            val value = requiredText(fieldName, address)
            if (value == CharacterImportIssue.ERROR_VALUE) return 0.0
            return value.toLocalizedBigDecimalOrNull()?.toDouble()
                ?: throw ImportFieldException(
                    CharacterSheetImportFailure.InvalidFieldValue(fieldName, address, value),
                )
        }

        return try {
            CharacterSheetImportResult.Success(
                character =
                    Character(
                        identity =
                            CharacterIdentity(
                                name = requiredText("Nome", ReinoSheetV1Fields.Identity.NAME),
                                player = optionalText(ReinoSheetV1Fields.Identity.PLAYER),
                                creationDate = "",
                                height = optionalText(ReinoSheetV1Fields.Identity.HEIGHT),
                                weight = optionalText(ReinoSheetV1Fields.Identity.WEIGHT),
                                race = optionalText(ReinoSheetV1Fields.Identity.RACE),
                                kingdom = optionalText(ReinoSheetV1Fields.Identity.KINGDOM),
                                age =
                                    optionalText(ReinoSheetV1Fields.Identity.AGE)
                                        .removeSuffix(".0"),
                                pointsToSpend =
                                    optionalText(ReinoSheetV1Fields.Identity.POINTS_TO_SPEND)
                                        .toLocalizedBigDecimalOrNull()
                                        ?.toDouble()
                                        ?: 0.0,
                            ),
                        attributes =
                            CharacterAttributes(
                                strength =
                                    requiredInt("ST", ReinoSheetV1Fields.Attributes.STRENGTH),
                                strengthCost = intValue(values, ReinoSheetV1Fields.AttributeCosts.STRENGTH_COST),
                                dexterity =
                                    requiredInt("DX", ReinoSheetV1Fields.Attributes.DEXTERITY),
                                dexterityCost = intValue(values, ReinoSheetV1Fields.AttributeCosts.DEXTERITY_COST),
                                intelligence =
                                    requiredInt("IQ", ReinoSheetV1Fields.Attributes.INTELLIGENCE),
                                intelligenceCost = intValue(values, ReinoSheetV1Fields.AttributeCosts.INTELLIGENCE_COST),
                                health =
                                    requiredInt("HT", ReinoSheetV1Fields.Attributes.HEALTH),
                                healthCost = intValue(values, ReinoSheetV1Fields.AttributeCosts.HEALTH_COST),
                                fatiguePoints =
                                    requiredInt(
                                        "Pontos de fadiga",
                                        ReinoSheetV1Fields.Attributes.MAX_FATIGUE_POINTS,
                                    ),
                                manaPoints =
                                    requiredInt(
                                        "Pontos de mana",
                                        ReinoSheetV1Fields.Attributes.MAX_MANA_POINTS,
                                    ),
                                will =
                                    requiredInt(
                                        "Vontade",
                                        ReinoSheetV1Fields.Attributes.WILL,
                                    ),
                                perception =
                                    requiredInt(
                                        "Percepção",
                                        ReinoSheetV1Fields.Attributes.PERCEPTION,
                                    ),
                                hitPoints =
                                    requiredInt(
                                        "Pontos de vida",
                                        ReinoSheetV1Fields.Attributes.MAX_HIT_POINTS,
                                    ),
                                basicLift =
                                    requiredDouble(
                                        "Base de carga",
                                        ReinoSheetV1Fields.Attributes.BASIC_LIFT,
                                    ),
                                basicSpeed =
                                    requiredDouble(
                                        "Velocidade básica",
                                        ReinoSheetV1Fields.Attributes.BASIC_SPEED,
                                    ),
                                basicMove =
                                    requiredInt(
                                        "Deslocamento básico",
                                        ReinoSheetV1Fields.Attributes.BASIC_MOVE,
                                    ),
                                dodge =
                                    requiredInt(
                                        "Esquiva",
                                        ReinoSheetV1Fields.Attributes.DODGE,
                                    ),
                                damageGDP =
                                    damage(
                                        value(values, ReinoSheetV1Fields.Attributes.DAMAGE_THRUST_DICE),
                                        value(values, ReinoSheetV1Fields.Attributes.DAMAGE_THRUST_MODIFIER),
                                    ),
                                damageBAL =
                                    damage(
                                        value(values, ReinoSheetV1Fields.Attributes.DAMAGE_SWING_DICE),
                                        value(values, ReinoSheetV1Fields.Attributes.DAMAGE_SWING_MODIFIER),
                                    ),
                                dodgeBAL = intValue(values, ReinoSheetV1Fields.Attributes.DODGE_BAL),
                                dodgeGDP = intValue(values, ReinoSheetV1Fields.Attributes.DODGE_GDP),
                                block = intValue(values, ReinoSheetV1Fields.Attributes.BLOCK),
                                weightLevels = createWeightLevels(values),
                                parry = createParry(values),
                                defenses = createDefenses(values),
                                totalCost = intValue(values, ReinoSheetV1Fields.Attributes.TOTAL_COST),
                            ),
                        importIssues = importIssues.values.toList(),
                        expertise = createExpertise(values),
                        raceAndAdvantages = createAdvantages(values),
                        reactionModifiers = createReactionModifiers(values),
                        disadvantagesAndPeculiarities = createDisadvantages(values),
                        rangedWeapons = createRangedWeapons(values),
                        meleeWeapons = createMeleeWeapons(values),
                        inventory = createInventory(values),
                        magicItems = createMagicItems(values),
                        armorList = createArmor(values),
                        money = createMoney(values),
                        annotations = createAnnotations(values),
                        pointResume = createPointResume(values),
                        spellbook = createSpellbook(spellbookValues),
                    ),
                format = ReinoSheetV1Fields.format,
            )
        } catch (error: ImportFieldException) {
            CharacterSheetImportResult.Failure(error.failure)
        }
    }

    private fun createWeightLevels(values: Map<String, String>) =
        rows(50, 58, 2).mapNotNull { row ->
            val name = value(values, ReinoSheetV1Fields.Encumbrance.name(row))
            if (name.isBlank()) null else CharacterAttributes.WeightStatus(
                weightLevel = name,
                weight = value(values, ReinoSheetV1Fields.Encumbrance.maxWeight(row)),
                dislocation = value(values, ReinoSheetV1Fields.Encumbrance.move(row)),
                dodge = value(values, ReinoSheetV1Fields.Encumbrance.dodge(row)),
            )
        }

    private fun createParry(values: Map<String, String>) =
        listOf(
            ReinoSheetV1Fields.Parry.BAL_NAME to ReinoSheetV1Fields.Parry.BAL_VALUE,
            ReinoSheetV1Fields.Parry.GDP_NAME to ReinoSheetV1Fields.Parry.GDP_VALUE,
            ReinoSheetV1Fields.Parry.ESCRIMA_NAME to ReinoSheetV1Fields.Parry.ESCRIMA_VALUE,
            ReinoSheetV1Fields.Parry.MAIN_GAUCHE_NAME to ReinoSheetV1Fields.Parry.MAIN_GAUCHE_VALUE,
            ReinoSheetV1Fields.Parry.BOXE_NAME to ReinoSheetV1Fields.Parry.BOXE_VALUE,
        ).mapNotNull { (nameAddress, valueAddress) ->
            val name = value(values, nameAddress)
            if (name.isBlank()) null else CharacterAttributes.Parry(name, value(values, valueAddress))
        }

    private fun createDefenses(values: Map<String, String>) =
        rows(70, 80, 2).mapNotNull { row ->
            val part = value(values, ReinoSheetV1Fields.Defense.part(row))
            if (part.isBlank()) null else CharacterAttributes.Defense(
                part = part,
                naturalDP = value(values, ReinoSheetV1Fields.Defense.naturalDP(row)),
                armorDP = value(values, ReinoSheetV1Fields.Defense.armorDP(row)),
                totalDP = value(values, ReinoSheetV1Fields.Defense.totalDP(row)),
                naturalRD = value(values, ReinoSheetV1Fields.Defense.naturalRD(row)),
                armorRD = value(values, ReinoSheetV1Fields.Defense.armorRD(row)),
                totalRD = value(values, ReinoSheetV1Fields.Defense.totalRD(row)),
            )
        }

    private fun intValue(values: Map<String, String>, address: String) =
        value(values, address).toDoubleOrNull()?.toInt() ?: 0

    private fun damage(dice: String, modifier: String): String =
        if (dice.isBlank()) "" else dice + when {
            modifier.isBlank() || modifier == "0" -> ""
            modifier.startsWith("-") -> modifier
            else -> "+$modifier"
        }

    private fun createAdvantages(values: Map<String, String>) =
        RaceAndAdvantages(
            list =
                rows(
                    ReinoSheetV1Fields.RaceAndAdvantages.FIRST_ROW,
                    ReinoSheetV1Fields.RaceAndAdvantages.LAST_ROW,
                    ReinoSheetV1Fields.RaceAndAdvantages.STEP,
                ).mapNotNull { row ->
                    val description =
                        values[ReinoSheetV1Fields.RaceAndAdvantages.description(row)]
                            .orEmpty().trim()
                    if (description.isBlank()) return@mapNotNull null
                    RaceAndAdvantages.AdvantageModel(
                        code = values[ReinoSheetV1Fields.RaceAndAdvantages.code(row)].orEmpty(),
                        name = description,
                        description = description,
                        cost = value(values, ReinoSheetV1Fields.RaceAndAdvantages.cost(row)),
                        extraValue =
                            value(values, ReinoSheetV1Fields.RaceAndAdvantages.extraValue(row)),
                    )
                },
            totalCost = value(values, ReinoSheetV1Fields.RaceAndAdvantages.TOTAL_COST),
        )

    private fun createReactionModifiers(values: Map<String, String>) =
        ReactionModifiers(
            appearance =
                ReactionModifiers.Modifier(
                    name = value(values, ReinoSheetV1Fields.ReactionModifiers.APPEARANCE_NAME),
                    value = value(values, ReinoSheetV1Fields.ReactionModifiers.APPEARANCE_VALUE),
                    cost = value(values, ReinoSheetV1Fields.ReactionModifiers.APPEARANCE_COST),
                ),
            status =
                ReactionModifiers.Modifier(
                    name = value(values, ReinoSheetV1Fields.ReactionModifiers.STATUS_NAME),
                    value = value(values, ReinoSheetV1Fields.ReactionModifiers.STATUS_VALUE),
                    cost = value(values, ReinoSheetV1Fields.ReactionModifiers.STATUS_COST),
                ),
            reputation =
                ReactionModifiers.Modifier(
                    name = value(values, ReinoSheetV1Fields.ReactionModifiers.REPUTATION_NAME),
                    value = value(values, ReinoSheetV1Fields.ReactionModifiers.REPUTATION_VALUE),
                    cost = value(values, ReinoSheetV1Fields.ReactionModifiers.REPUTATION_COST),
                ),
            additional =
                rows(70, 78, 2).mapNotNull { row ->
                    val name = value(values, ReinoSheetV1Fields.ReactionModifiers.additionalName(row))
                    if (name.isBlank()) null else ReactionModifiers.Modifier(
                        name = name,
                        value = value(values, ReinoSheetV1Fields.ReactionModifiers.additionalValue(row)),
                        cost = value(values, ReinoSheetV1Fields.ReactionModifiers.additionalCost(row)),
                    )
                },
            totalCost = value(values, ReinoSheetV1Fields.ReactionModifiers.TOTAL_COST),
        )

    private fun createDisadvantages(values: Map<String, String>) =
        DisadvantagesAndPeculiarities(
            disadvantages = createDpList(values, 88, 125),
            peculiarities = createDpList(values, 126, 135),
            disadvantagesTotalCost = value(values, ReinoSheetV1Fields.Disadvantages.TOTAL_COST),
            peculiaritiesTotalCost = value(values, ReinoSheetV1Fields.Peculiarities.TOTAL_COST),
            totalCost =
                listOf(
                    value(values, ReinoSheetV1Fields.Disadvantages.TOTAL_COST),
                    value(values, ReinoSheetV1Fields.Peculiarities.TOTAL_COST),
                ).sumOf { it.toDoubleOrNull() ?: 0.0 }.normalize(),
        )

    private fun createDpList(values: Map<String, String>, first: Int, last: Int) =
        rows(first, last, 2).mapNotNull { row ->
            val description = value(values, "AL$row")
            if (description.isBlank()) null else DisadvantagesAndPeculiarities.DPModel(
                cost = value(values, "AI$row"),
                name = description,
                description = description,
            )
        }

    private fun createRangedWeapons(values: Map<String, String>) =
        rows(147, 153, 2).mapNotNull { row ->
            val name = value(values, ReinoSheetV1Fields.RangedWeapons.name(row))
            if (name.isBlank()) null else RangedWeapon(
                name = name,
                damage = value(values, ReinoSheetV1Fields.RangedWeapons.damage(row)),
                precision = value(values, ReinoSheetV1Fields.RangedWeapons.precision(row)),
                halfDistance = value(values, ReinoSheetV1Fields.RangedWeapons.halfDistance(row)),
                maxDistance = value(values, ReinoSheetV1Fields.RangedWeapons.maxDistance(row)),
                fireRate = value(values, ReinoSheetV1Fields.RangedWeapons.fireRate(row)),
                cdt = value(values, ReinoSheetV1Fields.RangedWeapons.cdt(row)),
                tr = value(values, ReinoSheetV1Fields.RangedWeapons.tr(row)),
                recoil = value(values, ReinoSheetV1Fields.RangedWeapons.recoil(row)),
                st = value(values, ReinoSheetV1Fields.RangedWeapons.st(row)),
                notes = value(values, ReinoSheetV1Fields.RangedWeapons.notes(row)),
            )
        }

    private fun createMeleeWeapons(values: Map<String, String>) =
        rows(159, 167, 2).mapNotNull { row ->
            val name = value(values, ReinoSheetV1Fields.MeleeWeapons.name(row))
            if (name.isBlank()) null else MeleeWeapon(
                name = name,
                quality = value(values, ReinoSheetV1Fields.MeleeWeapons.quality(row)),
                gdp = value(values, ReinoSheetV1Fields.MeleeWeapons.gdp(row)),
                gdpType = value(values, ReinoSheetV1Fields.MeleeWeapons.gdpType(row)),
                bal = value(values, ReinoSheetV1Fields.MeleeWeapons.bal(row)),
                balType = value(values, ReinoSheetV1Fields.MeleeWeapons.balType(row)),
                notes = value(values, ReinoSheetV1Fields.MeleeWeapons.notes(row)),
            )
        }

    private fun createInventory(values: Map<String, String>) =
        Inventory(
            list = rows(174, 275, 12).mapNotNull { row ->
                val name = value(values, ReinoSheetV1Fields.Inventory.name(row))
                if (name.isBlank()) null else Inventory.InventoryModel(
                    name = name,
                    quantity = value(values, ReinoSheetV1Fields.Inventory.quantity(row)),
                    value = value(values, ReinoSheetV1Fields.Inventory.value(row)),
                    weight = value(values, ReinoSheetV1Fields.Inventory.weight(row)),
                    description = value(values, ReinoSheetV1Fields.Inventory.description(row)),
                )
            },
            totalWeight = value(values, ReinoSheetV1Fields.Inventory.TOTAL_WEIGHT),
            totalCost = value(values, ReinoSheetV1Fields.Inventory.TOTAL_COST),
        )

    private fun createMagicItems(values: Map<String, String>) =
        MagicItems(
            rows(174, 246, 12).mapNotNull { row ->
                val name = value(values, ReinoSheetV1Fields.MagicItems.name(row))
                if (name.isBlank()) null else MagicItems.MagicItemModel(
                    name = name,
                    weight = value(values, ReinoSheetV1Fields.MagicItems.weight(row)),
                    cost = value(values, ReinoSheetV1Fields.MagicItems.cost(row)),
                    mana = value(values, ReinoSheetV1Fields.MagicItems.mana(row)),
                    description = value(values, ReinoSheetV1Fields.MagicItems.description(row)),
                )
            },
        )

    private fun createArmor(values: Map<String, String>) =
        ArmorList(
            list = rows(262, 272, 2).mapNotNull { row ->
                val name = value(values, ReinoSheetV1Fields.Armor.name(row))
                if (name.isBlank()) null else ArmorList.ArmorModel(
                    name = name,
                    weight = value(values, ReinoSheetV1Fields.Armor.weight(row)),
                    cost = value(values, ReinoSheetV1Fields.Armor.cost(row)),
                    description = value(values, ReinoSheetV1Fields.Armor.description(row)),
                    defenseRD = value(values, ReinoSheetV1Fields.Armor.defenseRD(row)),
                    defenseDP = value(values, ReinoSheetV1Fields.Armor.defenseDP(row)),
                )
            },
            totalWeight = value(values, ReinoSheetV1Fields.Armor.TOTAL_WEIGHT),
            totalCost = value(values, ReinoSheetV1Fields.Armor.TOTAL_COST),
        )

    private fun createMoney(values: Map<String, String>) =
        Money(
            gold = Money.Coin(value(values, "BK248"), value(values, "BK253")),
            silver = Money.Coin(value(values, "BU248"), value(values, "BU253")),
            copper = Money.Coin(value(values, "CD248"), value(values, "CD253")),
        )

    private fun createAnnotations(values: Map<String, String>) =
        rows(147, 275, 1).map { value(values, "BL$it") }.filter(String::isNotBlank)

    private fun createPointResume(values: Map<String, String>) =
        PointResume(
            status = value(values, "CH262"),
            advantages = value(values, "CH264"),
            disadvantages = value(values, "CH266"),
            race = value(values, "CH268"),
            peculiarities = value(values, "CH270"),
            expertise = value(values, "CH272"),
            total = value(values, "CH274"),
        )

    private fun createSpellbook(values: Map<String, String>) =
        Spellbook(
            rows(4, 132, 2).mapNotNull { row ->
                val name = value(values, ReinoSheetV1Fields.Spellbook.name(row))
                if (name.isBlank()) null else Spellbook.SpellModel(
                    cost = value(values, ReinoSheetV1Fields.Spellbook.cost(row)),
                    name = name,
                    difficulty = value(values, ReinoSheetV1Fields.Spellbook.difficulty(row)),
                    level = value(values, ReinoSheetV1Fields.Spellbook.level(row)),
                    page = value(values, ReinoSheetV1Fields.Spellbook.page(row)),
                    spellClass = value(values, ReinoSheetV1Fields.Spellbook.spellClass(row)),
                    duration = value(values, ReinoSheetV1Fields.Spellbook.duration(row)),
                    castingCost = value(values, ReinoSheetV1Fields.Spellbook.castingCost(row)),
                    maintenanceCost = value(values, ReinoSheetV1Fields.Spellbook.maintenanceCost(row)),
                    castingTime = value(values, ReinoSheetV1Fields.Spellbook.castingTime(row)),
                    notes = value(values, ReinoSheetV1Fields.Spellbook.notes(row)),
                )
            },
        )

    private fun rows(first: Int, last: Int, step: Int) = (first..last step step)

    private fun value(values: Map<String, String>, address: String) =
        values[address].orEmpty().trim().let {
            if (it.isBrokenReference()) CharacterImportIssue.ERROR_VALUE else it.normalizeNumber()
        }

    private fun String.isBrokenReference() = trim().equals("#REF!", ignoreCase = true)

    private fun Double.normalize() =
        if (this % 1.0 == 0.0) toInt().toString() else toString()

    private fun createExpertise(values: Map<String, String>): Expertise {
        val list =
            buildList {
                var position = 0
                for (
                    row in
                    ReinoSheetV1Fields.Skills.FIRST_ROW..
                        ReinoSheetV1Fields.Skills.LAST_ROW step
                        ReinoSheetV1Fields.Skills.STEP
                ) {
                    val name = values[ReinoSheetV1Fields.Skills.name(row)].orEmpty().trim()
                    if (name.isBlank()) continue

                    val difficulty =
                        values[ReinoSheetV1Fields.Skills.difficulty(row)]
                            .orEmpty()
                            .trim()
                    val difficultyParts = difficulty.split("/", limit = 2)
                    val type = difficultyParts.getOrNull(0).orEmpty().trim()
                    val levelAndExtra =
                        difficultyParts.getOrNull(1)
                            .orEmpty()
                            .trim()
                            .split(Regex("\\s+"), limit = 2)

                    val modifiers =
                        listOf(
                            ReinoSheetV1Fields.Skills.modifierOne(row),
                            ReinoSheetV1Fields.Skills.modifierTwo(row),
                            ReinoSheetV1Fields.Skills.modifierThree(row),
                            ReinoSheetV1Fields.Skills.modifierFour(row),
                        ).mapNotNull { address ->
                            values[address]?.trim()?.takeIf(String::isNotBlank)
                        }

                    add(
                        Expertise.ExpertiseModel(
                            name = name,
                            difficultType = type,
                            difficultLevel = levelAndExtra.getOrNull(0).orEmpty(),
                            difficultExtra = levelAndExtra.getOrNull(1).orEmpty(),
                            cost =
                                values[ReinoSheetV1Fields.Skills.cost(row)]
                                    .orEmpty()
                                    .normalizeNumber(),
                            nh =
                                values[ReinoSheetV1Fields.Skills.level(row)]
                                    .orEmpty()
                                    .normalizeNumber(),
                            modifiers = modifiers,
                        ),
                    )
                    position++
                }
            }

        return Expertise(
            list = list,
            totalCost =
                values[ReinoSheetV1Fields.Skills.TOTAL_COST]
                    .orEmpty()
                    .normalizeNumber(),
        )
    }

    private fun validateRequiredSheets(
        sheetNames: Set<String>,
    ): CharacterSheetImportFailure? =
        REQUIRED_SHEETS
            .firstOrNull { it !in sheetNames }
            ?.let(CharacterSheetImportFailure::MissingSheet)

    private fun parseXml(bytes: ByteArray) =
        DocumentBuilderFactory
            .newInstance()
            .apply { isNamespaceAware = true }
            .newDocumentBuilder()
            .parse(ByteArrayInputStream(bytes))

    private fun Element.firstChildText(tagName: String): String? {
        val directNodes = getElementsByTagName(tagName)
        return if (directNodes.length > 0) directNodes.item(0).textContent else null
    }

    private fun String.toLocalizedBigDecimalOrNull(): BigDecimal? =
        replace(",", ".").toBigDecimalOrNull()

    private fun String.normalizeNumber(): String {
        val number = toLocalizedBigDecimalOrNull() ?: return this
        return number.stripTrailingZeros().toPlainString()
    }

    private class ImportFieldException(
        val failure: CharacterSheetImportFailure,
    ) : RuntimeException()

    private companion object {
        const val CONTENT_TYPES_PATH = "[Content_Types].xml"
        const val WORKBOOK_PATH = "xl/workbook.xml"
        const val WORKBOOK_RELS_PATH = "xl/_rels/workbook.xml.rels"
        const val SHARED_STRINGS_PATH = "xl/sharedStrings.xml"

        val REQUIRED_SHEETS =
            setOf(
                ReinoSheetV1Fields.CHARACTER_SHEET,
                ReinoSheetV1Fields.SPELLBOOK_SHEET,
                ReinoSheetV1Fields.INDEX_SHEET,
            )

        val REQUIRED_ADDRESSES =
            setOf(
                ReinoSheetV1Fields.Identity.NAME,
                ReinoSheetV1Fields.Identity.PLAYER,
                ReinoSheetV1Fields.Identity.HEIGHT,
                ReinoSheetV1Fields.Identity.WEIGHT,
                ReinoSheetV1Fields.Identity.RACE,
                ReinoSheetV1Fields.Identity.KINGDOM,
                ReinoSheetV1Fields.Identity.AGE,
                ReinoSheetV1Fields.Identity.POINTS_TO_SPEND,
                ReinoSheetV1Fields.Attributes.STRENGTH,
                ReinoSheetV1Fields.Attributes.MAX_FATIGUE_POINTS,
                ReinoSheetV1Fields.Attributes.MAX_MANA_POINTS,
                ReinoSheetV1Fields.Attributes.DEXTERITY,
                ReinoSheetV1Fields.Attributes.WILL,
                ReinoSheetV1Fields.Attributes.INTELLIGENCE,
                ReinoSheetV1Fields.Attributes.PERCEPTION,
                ReinoSheetV1Fields.Attributes.HEALTH,
                ReinoSheetV1Fields.Attributes.MAX_HIT_POINTS,
                ReinoSheetV1Fields.Attributes.BASIC_LIFT,
                ReinoSheetV1Fields.Attributes.BASIC_SPEED,
                ReinoSheetV1Fields.Attributes.BASIC_MOVE,
                ReinoSheetV1Fields.Attributes.DODGE,
            ) +
                buildSet {
                    for (
                        row in
                        ReinoSheetV1Fields.Skills.FIRST_ROW..
                            ReinoSheetV1Fields.Skills.LAST_ROW step
                            ReinoSheetV1Fields.Skills.STEP
                    ) {
                        add(ReinoSheetV1Fields.Skills.cost(row))
                        add(ReinoSheetV1Fields.Skills.name(row))
                        add(ReinoSheetV1Fields.Skills.difficulty(row))
                        add(ReinoSheetV1Fields.Skills.level(row))
                        add(ReinoSheetV1Fields.Skills.modifierOne(row))
                        add(ReinoSheetV1Fields.Skills.modifierTwo(row))
                        add(ReinoSheetV1Fields.Skills.modifierThree(row))
                        add(ReinoSheetV1Fields.Skills.modifierFour(row))
                    }
                    add(ReinoSheetV1Fields.Skills.TOTAL_COST)
                }
    }
}
