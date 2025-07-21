package com.blasck.reino.presentation.node

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blasck.reino.framework.mock.SyrioAugustoModel
import com.blasck.reino.presentation.components.card.VerticalStatus
import com.blasck.reino.presentation.screen.model.CharacterModel
import com.blasck.reino.system.theme.KingdomTheme

@Composable
fun ParryNode(parry: List<CharacterModel.StatusInformation.Parry>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until 3) {
            val item = parry.getOrNull(i)

            Row(
                modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    if (item != null) {
                        VerticalStatus(item.name, item.value)
                    }
                }

                // Adiciona divisor, menos no último item
                if (i < 2) {
                    VerticalDivider(
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ParryNodePreview(){
    KingdomTheme {
        ParryNode(
            SyrioAugustoModel.model.status.parry
        )

    }
}