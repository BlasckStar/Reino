package com.blasck.reino.presentation.node

import CollapseFrame
import EditableCard
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blasck.reino.framework.mock.SyrioAugustoModel
import com.blasck.reino.presentation.components.card.HorizontalCard
import com.blasck.reino.presentation.components.card.VerticalCard
import com.blasck.reino.presentation.components.card.VerticalExtraCard
import com.blasck.reino.presentation.screen.model.CharacterModel
import com.blasck.reino.system.theme.KingdomTheme

@Composable
fun MoreAttributesNode(status: CharacterModel.StatusInformation) {
    CollapseFrame("Mais Atributos") {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EditableCard("P. Fadiga", status.fatigue)
                EditableCard("P. Mana", status.mana)
                EditableCard("P. Vida", status.hitPoints)
                HorizontalCard("Vontade", status.will)
                HorizontalCard("Percepção", status.perception)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                modifier = Modifier
                    .wrapContentWidth()
                    .fillMaxWidth()
            ) {
                VerticalCard("B. Carga", status.baseWeight)
                VerticalCard("Dano GDP", status.damageGDP)
                VerticalCard("Dano BAL", status.damageBAL)
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                modifier = Modifier
                    .wrapContentWidth()
                    .fillMaxWidth()
            ) {
                VerticalCard("Velocidade B.", status.basicSpeed)
                VerticalCard("Deslocamento B.", status.basicDislocation)
            }
            WeightNode(status.weightLevels)
            VerticalExtraCard("Esquiva") { EvadeNode(status) }
            VerticalExtraCard("Aparar") { ParryNode(status.parry) }
            HorizontalCard("Bloqueio", status.block)
            ProtectionNode(status)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MoreAttributesPreview(){
    val model = SyrioAugustoModel.model
    KingdomTheme {
        MoreAttributesNode(model.status)
    }
}