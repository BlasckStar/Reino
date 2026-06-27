package com.blasck.reino.data.importer

import com.blasck.reino.domain.model.CharacterSheetFormat

/**
 * Contrato das células usadas pela primeira versão da ficha do Reino.
 *
 * O restante do aplicativo não deve conhecer esses endereços.
 * Quando o modelo da planilha mudar, um novo mapeamento poderá ser criado
 * sem alterar o domínio.
 */

object ReinoSheetV1Fields {
    val format = CharacterSheetFormat.REINO_V1

    const val CHARACTER_SHEET = "Ficha"
    const val PRINT_SHEET = "Ficha Impressão"
    const val SPELLBOOK_SHEET = "Grimório"
    const val INDEX_SHEET = "Indice"

    object Identity {
        const val NAME = "AA2"
        const val PLAYER = "BH2"

        /**
         * BV2 contém apenas o rótulo "Data de Criação".
         * Na planilha enviada não encontrei uma célula de valor preenchida
         * para data de criação.
         */
        const val CREATION_DATE_LABEL = "BV2"

        const val HEIGHT = "AA4"
        const val WEIGHT = "AJ4"
        const val RACE = "AS4"
        const val KINGDOM = "BE4"
        const val AGE = "BS4"
        const val POINTS_TO_SPEND = "CF4"

        /**
         * V6:AB7 parece ser o campo/rótulo de aparência.
         * Mantido como label porque no arquivo enviado não havia valor preenchido.
         */
        const val APPEARANCE_LABEL = "V6"
    }

    object AttributeCosts {
        const val STRENGTH_COST = "B12"
        const val DEXTERITY_COST = "B18"
        const val INTELLIGENCE_COST = "B24"
        const val HEALTH_COST = "B30"
    }

    object Attributes {
        const val STRENGTH = "L10"
        const val DEXTERITY = "L16"
        const val INTELLIGENCE = "L22"
        const val HEALTH = "L28"

        const val FATIGUE_POINTS = "Z10"
        const val MAX_FATIGUE_POINTS = "AD10"

        const val MANA_POINTS = "Z13"
        const val MAX_MANA_POINTS = "AD13"

        const val WILL = "Z16"
        const val PERCEPTION = "Z22"

        const val HIT_POINTS = "Z28"
        const val MAX_HIT_POINTS = "AD28"

        const val BASIC_LIFT = "E36"

        const val DAMAGE_THRUST_DICE = "O36"
        const val DAMAGE_THRUST_MODIFIER = "T36"

        const val DAMAGE_SWING_DICE = "X36"
        const val DAMAGE_SWING_MODIFIER = "AC36"

        const val BASIC_SPEED = "E43"
        const val BASIC_MOVE = "S43"

        /**
         * Alias mantido para compatibilidade com o importer atual.
         */
        const val DODGE = "E63"

        const val DODGE_BAL = "E63"
        const val DODGE_GDP = "J63"

        const val BLOCK = "X63"

        const val TOTAL_COST = "CH262"
    }

    object Encumbrance {
        const val FIRST_ROW = 50
        const val LAST_ROW = 58
        const val STEP = 2

        fun name(row: Int) = "E$row"
        fun maxWeight(row: Int) = "N$row"
        fun moveLabel(row: Int) = "R$row"
        fun move(row: Int) = "V$row"
        fun dodgeLabel(row: Int) = "Z$row"
        fun dodge(row: Int) = "AE$row"
    }

    object Parry {
        const val BAL_NAME = "E66"
        const val BAL_VALUE = "E63"

        const val GDP_NAME = "J66"
        const val GDP_VALUE = "J63"

        const val ESCRIMA_NAME = "O66"
        const val ESCRIMA_VALUE = "O63"

        const val MAIN_GAUCHE_NAME = "R66"
        const val MAIN_GAUCHE_VALUE = "R63"

        const val BOXE_NAME = "U66"
        const val BOXE_VALUE = "U63"
    }

    object Defense {
        const val FIRST_ROW = 70
        const val LAST_ROW = 80
        const val STEP = 2

        fun naturalDP(row: Int) = "F$row"
        fun armorDP(row: Int) = "I$row"
        fun totalDP(row: Int) = "L$row"

        fun part(row: Int) = "P$row"

        fun naturalRD(row: Int) = "W$row"
        fun armorRD(row: Int) = "Z$row"
        fun totalRD(row: Int) = "AC$row"
    }

    object ReactionModifiers {
        const val APPEARANCE_NAME = "AI64"
        const val APPEARANCE_VALUE = "AO64"
        const val APPEARANCE_COST = "BF64"

        const val STATUS_NAME = "AI66"
        const val STATUS_VALUE = "AO66"
        const val STATUS_COST = "BF66"

        const val REPUTATION_NAME = "AI68"
        const val REPUTATION_VALUE = "AO68"
        const val REPUTATION_COST = "BF68"

        const val ADDITIONAL_FIRST_ROW = 70
        const val ADDITIONAL_LAST_ROW = 78
        const val STEP = 2

        fun additionalName(row: Int) = "AI$row"
        fun additionalValue(row: Int) = "AO$row"
        fun additionalCost(row: Int) = "BF$row"

        const val TOTAL_COST = "BF80"
    }

    object RaceAndAdvantages {
        const val FIRST_ROW = 88
        const val LAST_ROW = 135
        const val STEP = 2

        fun code(row: Int) = "D$row"
        fun cost(row: Int) = "F$row"
        fun description(row: Int) = "I$row"

        /**
         * Coluna usada para valores auxiliares de algumas vantagens.
         *
         * Exemplos:
         * AD106 pode representar bônus/pontos.
         * AD116 pode representar pontos extras.
         */
        fun extraValue(row: Int) = "AD$row"

        const val TOTAL_COST = "CH264"
    }

    object Disadvantages {
        const val FIRST_ROW = 88
        const val LAST_ROW = 125
        const val STEP = 2

        fun cost(row: Int) = "AI$row"
        fun description(row: Int) = "AL$row"

        const val TOTAL_COST = "CH266"
    }

    object Peculiarities {
        const val FIRST_ROW = 126
        const val LAST_ROW = 135
        const val STEP = 2

        fun cost(row: Int) = "AI$row"
        fun description(row: Int) = "AL$row"

        const val TOTAL_COST = "CH270"
    }

    object Skills {
        const val FIRST_ROW = 14
        const val LAST_ROW = 135
        const val STEP = 2

        fun cost(row: Int) = "BL$row"
        fun name(row: Int) = "BO$row"
        fun difficulty(row: Int) = "CD$row"
        fun level(row: Int) = "CJ$row"

        /**
         * Modificadores ocultos usados nas fórmulas de NH.
         * No XLSX aparecem nas colunas CM, CN, CO e CP.
         */
        fun modifierOne(row: Int) = "CM$row"
        fun modifierTwo(row: Int) = "CN$row"
        fun modifierThree(row: Int) = "CO$row"
        fun modifierFour(row: Int) = "CP$row"

        const val TOTAL_COST = "CH272"
    }

    object RangedWeapons {
        const val FIRST_ROW = 147
        const val LAST_ROW = 153
        const val STEP = 2

        fun name(row: Int) = "F$row"
        fun damage(row: Int) = "T$row"
        fun precision(row: Int) = "Y$row"
        fun accuracy(row: Int) = "Y$row"
        fun halfDistance(row: Int) = "AC$row"
        fun halfDamageDistance(row: Int) = "AC$row"
        fun maxDistance(row: Int) = "AG$row"
        fun fireRate(row: Int) = "AK$row"
        fun shots(row: Int) = "AK$row"
        fun cdt(row: Int) = "AN$row"
        fun rateOfFire(row: Int) = "AN$row"
        fun tr(row: Int) = "AQ$row"
        fun reloadTime(row: Int) = "AQ$row"
        fun recoil(row: Int) = "AT$row"
        fun st(row: Int) = "AW$row"
        fun requiredStrength(row: Int) = "AW$row"
        fun notes(row: Int) = "AZ$row"
    }

    object MeleeWeapons {
        const val FIRST_ROW = 159
        const val LAST_ROW = 167
        const val STEP = 2

        fun name(row: Int) = "F$row"
        fun quality(row: Int) = "T$row"
        fun gdp(row: Int) = "AA$row"
        fun thrustDamage(row: Int) = "AA$row"
        fun gdpType(row: Int) = "AG$row"
        fun thrustDamageType(row: Int) = "AG$row"
        fun bal(row: Int) = "AM$row"
        fun swingDamage(row: Int) = "AM$row"
        fun balType(row: Int) = "AS$row"
        fun swingDamageType(row: Int) = "AS$row"
        fun notes(row: Int) = "AY$row"
    }

    object Inventory {
        const val FIRST_ROW = 174
        const val LAST_ROW = 275
        const val STEP = 12

        fun name(row: Int) = "F$row"

        /**
         * A ficha enviada não deixou uma coluna clara para quantidade.
         * Mantido aqui para compatibilidade caso seu importer espere quantidade.
         */
        fun quantity(row: Int) = "D$row"

        fun value(row: Int) = "AB$row"
        fun cost(row: Int) = "AB$row"
        fun weight(row: Int) = "X$row"

        /**
         * A descrição do item fica no bloco abaixo do nome.
         * Exemplo esperado: F176 para item iniciado em F174.
         */
        fun description(row: Int) = "F${row + 2}"

        const val TOTAL_WEIGHT = "BA274"
        const val TOTAL_COST = "BE274"
    }

    object MagicItems {
        const val FIRST_ROW = 174
        const val LAST_ROW = 246
        const val STEP = 12

        fun name(row: Int) = "AI$row"
        fun weight(row: Int) = "AV$row"
        fun cost(row: Int) = "AZ$row"
        fun mana(row: Int) = "BE$row"

        /**
         * A descrição do item mágico fica no bloco abaixo do nome.
         * Exemplo:
         * AI174 = nome
         * AI176 = descrição
         */
        fun description(row: Int) = "AI${row + 2}"
    }

    object Armor {
        /**
         * No XLSX enviado, a seção de armadura aparece no bloco AI259:BH274.
         * Os dados de peso e custo estão preenchidos, mas os nomes das peças
         * não estavam preenchidos no arquivo original.
         */
        const val FIRST_ROW = 262
        const val LAST_ROW = 272
        const val STEP = 2

        fun name(row: Int) = "AI$row"
        fun weight(row: Int) = "BA$row"
        fun cost(row: Int) = "BE$row"
        fun description(row: Int) = "AI${row + 1}"

        fun defenseRD(row: Int) = "AZ$row"
        fun defenseDP(row: Int) = "AV$row"

        const val TOTAL_WEIGHT = "BA274"
        const val TOTAL_COST = "BE274"
    }

    object Money {
        const val GOLD_QUANTITY = "BK248"
        const val SILVER_QUANTITY = "BU248"
        const val COPPER_QUANTITY = "CD248"

        const val GOLD_VALUE = "BK253"
        const val SILVER_VALUE = "BU253"
        const val COPPER_VALUE = "CD253"
    }

    object Annotations {
        const val FIRST_ROW = 147
        const val LAST_ROW = 275
        const val STEP = 1

        fun value(row: Int) = "BL$row"
    }

    object PointResume {
        const val ATTRIBUTES_LABEL = "BL262"
        const val ATTRIBUTES_VALUE = "CH262"

        const val ADVANTAGES_LABEL = "BL264"
        const val ADVANTAGES_VALUE = "CH264"

        const val DISADVANTAGES_LABEL = "BL266"
        const val DISADVANTAGES_VALUE = "CH266"

        const val RACE_LABEL = "BL268"
        const val RACE_VALUE = "CH268"

        const val PECULIARITIES_LABEL = "BL270"
        const val PECULIARITIES_VALUE = "CH270"

        const val SKILLS_LABEL = "BL272"
        const val SKILLS_VALUE = "CH272"

        const val TOTAL_LABEL = "BL274"
        const val TOTAL_VALUE = "CH274"
    }

    object Spellbook {
        const val FIRST_ROW = 4
        const val LAST_ROW = 132
        const val STEP = 2

        fun cost(row: Int) = "F$row"
        fun name(row: Int) = "I$row"
        fun difficulty(row: Int) = "AH$row"
        fun level(row: Int) = "AK$row"
        fun page(row: Int) = "AN$row"
        fun spellClass(row: Int) = "AQ$row"
        fun duration(row: Int) = "AZ$row"
        fun castingCost(row: Int) = "BI$row"
        fun maintenanceCost(row: Int) = "BO$row"
        fun castingTime(row: Int) = "BV$row"
        fun notes(row: Int) = "CB$row"
    }

    object Index {
        const val TOTAL_XP = "H3"

        const val FIRST_ROW = 2
        const val LAST_ROW = 16
        const val STEP = 1

        fun abbreviation(row: Int) = "A$row"
        fun advantage(row: Int) = "B$row"
        fun bonus(row: Int) = "C$row"
    }
}