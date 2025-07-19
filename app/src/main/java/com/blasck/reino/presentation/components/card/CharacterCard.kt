package com.blasck.reino.presentation.components.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.blasck.reino.presentation.components.CoilImage
import com.blasck.reino.presentation.models.response.CharacterList
import com.blasck.reino.presentation.utils.isNotNullOrEmpty

@Composable
fun CharacterCard(
    info: CharacterList.CharacterInfo,
    onClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(info.id) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CoilImage(
                    imageUrl = info.img,
                    maxWidth = 64.dp,
                    maxHeight = 64.dp
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = info.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if(info.player.isNotNullOrEmpty()){
                        Text(
                            text = info.player,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                }
            }
            Text(
                text = info.id.trim(),
                modifier = Modifier
                    .padding(horizontal = 12.dp),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End
            )
        }
    }
}