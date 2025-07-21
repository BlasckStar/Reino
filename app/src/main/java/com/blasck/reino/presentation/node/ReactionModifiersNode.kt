package com.blasck.reino.presentation.node

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blasck.reino.framework.mock.SyrioAugustoModel
import com.blasck.reino.presentation.components.card.HorizontalItemCard
import com.blasck.reino.presentation.screen.model.CharacterModel
import com.blasck.reino.presentation.utils.Constants
import com.blasck.reino.system.theme.KingdomTheme

@Composable
fun ReactionModifiersNode(reaction: CharacterModel.ReactionModifiers) {

    val size = 100.dp
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        HorizontalItemCard(cost = "Aparência",name = reaction.appearance, size = size)
        HorizontalItemCard(cost = "Status", name = reaction.status, size = size)
        HorizontalItemCard(cost = "Reputação", name =reaction.reputation, size = size)
        LazyColumn {
            items(reaction.additional){
                HorizontalItemCard(cost = it.type, name = it.name, nh = "+"+it.cost, size = size)
            }
        }
        HorizontalItemCard("Total", Constants.EMPTY_STRING, nh = "+"+reaction.totalCost, size = size)
    }
}

@Preview(showBackground = true)
@Composable
fun ReactionModifiersNodePreview(){
    KingdomTheme {
        ReactionModifiersNode(SyrioAugustoModel.model.reactionModifiers)
    }
}