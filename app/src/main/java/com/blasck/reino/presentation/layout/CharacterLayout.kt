package com.blasck.reino.presentation.layout

import EditableCard
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blasck.reino.framework.mock.SyrioAugustoModel
import com.blasck.reino.presentation.components.CoilImage
import com.blasck.reino.presentation.components.card.StatusCard
import com.blasck.reino.presentation.node.MoreAttributesNode
import com.blasck.reino.presentation.screen.model.CharacterModel
import com.blasck.reino.system.theme.KingdomTheme

@Composable
fun CharacterLayout(
    model: CharacterModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CoilImage(model.image)
        Spacer(Modifier.size(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterHorizontally
            ),
            modifier = Modifier
                .wrapContentWidth()
                .fillMaxWidth()
        ) {
            StatusCard("ST", model.status.strength, model.status.strengthCost)
            StatusCard("DX", model.status.dexterity, model.status.dexterityCost)
            StatusCard("IQ", model.status.intelligence, model.status.intelligenceCost)
            StatusCard("HT", model.status.constitution, model.status.constitutionCost)
        }
        Spacer(Modifier.size(8.dp))
        MoreAttributesNode(model.status)
    }
}

@Preview(showBackground = true)
@Composable
fun CharacterLayoutPreview() {
    KingdomTheme {
        CharacterLayout(SyrioAugustoModel.model)
    }
}