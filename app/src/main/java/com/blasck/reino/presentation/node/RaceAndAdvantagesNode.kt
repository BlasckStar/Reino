package com.blasck.reino.presentation.node

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blasck.reino.framework.mock.SyrioAugustoModel
import com.blasck.reino.presentation.components.card.HorizontalItemCard
import com.blasck.reino.presentation.screen.model.CharacterModel
import com.blasck.reino.presentation.utils.Constants
import com.blasck.reino.system.theme.KingdomTheme

@Composable
fun RaceAndAdvantagesNode(data: CharacterModel.RaceAndAdvantages) {
    Column {

        Row(
            modifier = Modifier.padding(start = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Custo")
            Text("Nome")
        }

        HorizontalDivider(
            modifier = Modifier.padding(8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )

        LazyColumn {
            items(data.list){ item ->
                val nh =
                    if(item.chance.isNotEmpty() && item.multiplier.isNotEmpty()){
                        Constants.EMPTY_STRING
                    }else{
                        when{
                            item.chance.isNotEmpty() -> item.chance+"-"
                            item.multiplier.isNotEmpty() -> "+"+item.multiplier
                            else -> Constants.EMPTY_STRING
                        }
                    }
                HorizontalItemCard(
                    name = item.name,
                    cost = item.cost,
                    nh = nh,
                    description = item.description
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RaceAndAdvantagesNodePreview(){
    KingdomTheme {
        RaceAndAdvantagesNode(SyrioAugustoModel.model.raceAndAdvantages)
    }
}