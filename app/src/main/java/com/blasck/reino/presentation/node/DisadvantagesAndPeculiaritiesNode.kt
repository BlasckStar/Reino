package com.blasck.reino.presentation.node

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.blasck.reino.framework.mock.SyrioAugustoModel
import com.blasck.reino.presentation.components.card.HorizontalItemCard
import com.blasck.reino.presentation.screen.model.CharacterModel

@Composable
fun DisadvantagesAndPeculiaritiesNode(list: List<CharacterModel.DisadvantagesAndPeculiarities.DPModel>) {
    LazyColumn {
        items(list){
            HorizontalItemCard(
                cost = "-"+it.cost,
                name = it.name,
                description = it.description
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DisadvantagesAndPeculiaritiesNodePreview() {
    DisadvantagesAndPeculiaritiesNode(SyrioAugustoModel.model.disadvantagesAndPeculiarities.list)
}