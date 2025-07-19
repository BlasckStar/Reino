package com.blasck.reino.framework.mock

import com.blasck.reino.presentation.screen.model.CharacterModel

object SyrioAugustoModel {
    val model: CharacterModel = CharacterModel(
        information = CharacterModel.CharacterInformation(
            name = "Syrio Augusto",
            player = "Luiz",
            creationDate = "01/01/2023",
            height = "1,75cm",
            weight = "70kg",
            race = "Humana",
            kingdom = "Justos",
            age = "20",
            pointToSpend = "13",
            appearance = ""
        ),
        image = "https://i.ibb.co/XZqdTzv1/Imagem-do-Whats-App-de-2025-07-14-s-21-54-37-03ec8605.jpg",
        status = CharacterModel.StatusInformation(
            strength = "12",
            strengthCost = "20",
            strengthNextLevelCost = "10",
            dexterity = "18",
            dexterityCost = "150",
            dexterityNextLevelCost = "25",
            constitution = "16",
            constitutionCost = "80",
            constitutionNextLevelCost = "20",
            intelligence = "13",
            intelligenceCost = "30",
            intelligenceNextLevelCost = "10",
            fatigue = "12",
            mana = "12",
            will = "13",
            perception = "13",
            hitPoints = "16",
            baseWeight = "12",
            damageGDP = "1D-1",
            damageBAL = "1D+2",
            basicDislocation = "10",
            basicSpeed = "10.625",
            weightLevels = listOf(
                CharacterModel.StatusInformation.WeightStatus(
                    weightLevel = "Nenhum",
                    weight = "12",
                    dislocation = "10",
                    dodge = "11",
                ),
                CharacterModel.StatusInformation.WeightStatus(
                    weightLevel = "Leve",
                    weight = "24",
                    dislocation = "9",
                    dodge = "10",
                ),
                CharacterModel.StatusInformation.WeightStatus(
                    weightLevel = "Media",
                    weight = "36",
                    dislocation = "8",
                    dodge = "9",
                ),
                CharacterModel.StatusInformation.WeightStatus(
                    weightLevel = "Pesada",
                    weight = "72",
                    dislocation = "7",
                    dodge = "8",
                ),
                CharacterModel.StatusInformation.WeightStatus(
                    weightLevel = "M. Pesado",
                    weight = "120",
                    dislocation = "6",
                    dodge = "7",
                )
            ),
            dodgeBAL = "11",
            dodgeGDP = "13",
            parry = listOf(
                CharacterModel.StatusInformation.Parry(
                    name = "Esgrima",
                    value = "17",
                ),
                CharacterModel.StatusInformation.Parry(
                    name = "Main Gauche",
                    value = "13",
                ),
                CharacterModel.StatusInformation.Parry(
                    name = "Base",
                    value = "14",
                ),
            ),
            block = "7",
            headDefense = CharacterModel.StatusInformation.Defense(
                naturalDP = "0",
                armorDP = "5",
                naturalRD = "0",
                armorRD = "6",
            ),
            bodyDefense = CharacterModel.StatusInformation.Defense(
                naturalDP = "0",
                armorDP = "7",
                naturalRD = "0",
                armorRD = "7",

            ),
            legsDefense = CharacterModel.StatusInformation.Defense(
                naturalDP = "0",
                armorDP = "5",
                naturalRD = "0",
                armorRD = "6",
            ),
            armDefense = CharacterModel.StatusInformation.Defense(
                naturalDP = "0",
                armorDP = "5",
                naturalRD = "0",
                armorRD = "6",
            ),
            handsDefense = CharacterModel.StatusInformation.Defense(
                naturalDP = "0",
                armorDP = "5",
                naturalRD = "0",
                armorRD = "6",
            ),
            feetDefense = CharacterModel.StatusInformation.Defense(
                naturalDP = "0",
                armorDP = "5",
                naturalRD = "0",
                armorRD = "6",
            ),
            totalCost = "280"
        ),
        raceAndAdvantages = CharacterModel.RaceAndAdvantages(
            list = listOf(
                CharacterModel.RaceAndAdvantages.AdvantageModel(
                    name = "Aparência commun"
                ),
                CharacterModel.RaceAndAdvantages.AdvantageModel(
                    name = "Muito Rico",
                    cost = "30",
                ),
                CharacterModel.RaceAndAdvantages.AdvantageModel(
                    name = "Reputação: Matador de titã",
                    cost = "5",
                ),
                CharacterModel.RaceAndAdvantages.AdvantageModel(
                    name = "Status: Cidadão"
                ),
                CharacterModel.RaceAndAdvantages.AdvantageModel(
                    name = "Mestre de armas",
                    cost = "20",
                ),
                CharacterModel.RaceAndAdvantages.AdvantageModel(
                    name = "Reflexo de combate",
                    cost = "15",
                    description = "O personagem tem reações extremamente rápidas e raramente ficará\n" +
                            "surpreso por mais do que um breve instante. Ele receberá um bônus de + 1\n" +
                            "em qualquer Defesa Ativa. Tem direito, também, a um bônus de +1 na\n" +
                            "perícia Sacar Rápido, um bônus igual a +2 em Verificações de Pânico (pág.\n" +
                            "93) e nunca ficará “paralisado” devido à surpresa (pág. 122).\n" +
                            "Além disso, seu lado recebe um bônus igual a +1 em testes de iniciativa\n" +
                            "feitos para evitar um ataque de surpresa, ou +2, se ele for o líder. Nos testes\n" +
                            "de IQ feitos para despertar, ou se recuperar de uma surpresa ou de\n" +
                            "“atordoamento” mental, o personagem receberá um bônus de +6"
                ),
                CharacterModel.RaceAndAdvantages.AdvantageModel(
                    name = "Sorte",
                    cost = "30",
                ),
                CharacterModel.RaceAndAdvantages.AdvantageModel(
                    name = "Escola Fionica",
                    cost = "5",
                    chance = "6-"
                ),
                CharacterModel.RaceAndAdvantages.AdvantageModel(
                    name = "Hipoalgia",
                    cost = "10",
                    description = "O personagem é tão suscetível a danos, quanto qualquer outra pessoa,\n" +
                            "mas ele não sente a dor com a mesma intensidade. Ele não ficará atordoado,\n" +
                            "nem seu atributo DX será submetido ao redutor normalmente aplicado no\n" +
                            "turno seguinte, caso venha a ser ferido em combate (exceção: um golpe na\n" +
                            "cabeça ou um golpe fulminante continuarão provocando o atordoamento do\n" +
                            "personagem). No caso de ser submetido a tortura física, ele receberá um\n" +
                            "bônus igual a +3 no teste de sua resistência. O GM poderá permitir que ele\n" +
                            "receba um bônus de +3 em testes de Vontade, para ver se é capaz de ignorar\n" +
                            "a dor."
                ),
                CharacterModel.RaceAndAdvantages.AdvantageModel(
                    name = "Força de vontade",
                    cost = "30",
                    multiplier = "4",
                    multiplierType = "will",
                    description = "O personagem tem muito mais determinação que a média das pessoas.\n" +
                            "Seu nível de Vontade é adicionado a seu atributo IQ toda vez que ele fizer,\n" +
                            "por qualquer razão, um teste de Vontade, incluindo tentativas de influenciálo através da Diplomacia, Lábia, Sex Appeal, Interrogatório (com ou sem\n" +
                            "tortura), Hipnotismo ou tentativas de dominar sua mente através de magia ou\n" +
                            "psiquismo. A Força de Vontade é adicionada à resistência do personagem\n" +
                            "quando ele quer resistir a uma mágica (pág. 150). Esta vantagem, no entanto,\n" +
                            "não ajuda no caso de choque em combate, etc. Em casos questionáveis, a\n" +
                            "decisão do GM é lei"
                ),
                CharacterModel.RaceAndAdvantages.AdvantageModel(
                    name = "Pau do rei dos Incubus"
                )
            )
        ),
        reactionModifiers = CharacterModel.ReactionModifiers(
            appearance = "Comun",
            status = "Cidadão",
            reputation = "Matador de titã",
            additional = listOf(
                CharacterModel.ReactionModifiers.AdditionalModifier(
                    name = "Amigos",
                    type = "Senso de dever",
                    cost = "2",
                ),
                CharacterModel.ReactionModifiers.AdditionalModifier(
                    name = "Armadura linda",
                    type = "LdDA",
                    cost = "3",
                )
            ),
            totalCost = "5"
        ),
        disadvantagesAndPeculiarities = CharacterModel.DisadvantagesAndPeculiarities(
            listOf(
                CharacterModel.DisadvantagesAndPeculiarities.DPModel(
                    cost = "5",
                    name = "Senso de dever: Amigos",
                ),
                CharacterModel.DisadvantagesAndPeculiarities.DPModel(
                    cost = "10",
                    name = "Excesso de confiança",
                ),
                CharacterModel.DisadvantagesAndPeculiarities.DPModel(
                    cost = "15",
                    name = "Luxuria",
                ),
                CharacterModel.DisadvantagesAndPeculiarities.DPModel(
                    cost = "10",
                    name = "Código de Honra",
                ),
                CharacterModel.DisadvantagesAndPeculiarities.DPModel(
                    cost = "10",
                    name = "Mau Humor",
                ),
                CharacterModel.DisadvantagesAndPeculiarities.DPModel(
                    cost = "1",
                    name = "Não Liga para religação",
                ),
                CharacterModel.DisadvantagesAndPeculiarities.DPModel(
                    cost = "1",
                    name = "Fala de si mesmo na terceira pessoa",
                ),
                CharacterModel.DisadvantagesAndPeculiarities.DPModel(
                    cost = "1",
                    name = "Força sotaque(Espanhol)",
                ),
                CharacterModel.DisadvantagesAndPeculiarities.DPModel(
                    cost = "1",
                    name = "Complexo de Aquiles",
                ),CharacterModel.DisadvantagesAndPeculiarities.DPModel(
                    cost = "1",
                    name = "Sociavel",
                ),
            )
        ),
        expertise = CharacterModel.Expertise(
            listOf(
                CharacterModel.Expertise.ExpertiseModel(
                    name = "Esgrima",
                    description = "Blablabla",
                    difficultType = "F",
                    difficultLevel = "M",
                    cost = "32",
                    nh = "24",
                ),
                CharacterModel.Expertise.ExpertiseModel(
                    name = "Arte da Esgrima",
                    description = "Esgrima",
                    difficultType = "F",
                    difficultLevel = "M",
                    cost = "2",
                    nh = "19",
                )
            )
        ),
        rangedWeapon = CharacterModel.RangedWeapon(
            listOf(
                CharacterModel.RangedWeapon.RangedWeaponModel(
                    name = "Arco curto",
                    damage = "1D-1",
                    precision = "+1",
                    halfDistance = "120",
                    maxDistance = "180",
                    fireRate = "1",
                    cdt = "0,5",
                    tr = "12",
                    recoil = "0",
                    st = "7",
                    notes = "Perfuração",
                )
            )
        ),
        meleeWeapon = CharacterModel.MeleeWeapon(
            list = listOf(
                CharacterModel.MeleeWeapon.MeleeWeaponModel(
                    name = "Florete",
                    quality = "Altissima",
                    gdp = "1D+6",
                    gdpType = "Perfuração"
                ),
                CharacterModel.MeleeWeapon.MeleeWeaponModel(
                    name = "Main Gaunche Obsidiana",
                    quality = "Altissima",
                    gdp = "1D+2",
                    gdpType = "Perfuração",
                    bal = "1D+5",
                    balType = "Corte",
                    notes = "Ignora 5 de RD"
                )
            )
        )
    )
}
