package com.blasck.reino.presentation.components.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blasck.reino.framework.mock.SyrioAugustoModel
import com.blasck.reino.presentation.screen.model.CharacterModel
import com.blasck.reino.system.theme.KingdomTheme

@Composable
fun ExpertiseCard(expertise: CharacterModel.Expertise.ExpertiseModel) {
    val showDialog = remember { mutableStateOf(false) }

    if (expertise.description.isNotEmpty() && showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("Fechar")
                }
            },
            title = { Text("Descrição") },
            text = { Text(expertise.description) },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
    Card(
        modifier = Modifier
        .fillMaxWidth()
        .clickable(enabled = expertise.description.isNotEmpty()) {
            showDialog.value = true
        }
        .padding(vertical = 4.dp),
        shape = RoundedCornerShape(2.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
//        colors = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    expertise.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Row(
                    modifier = Modifier.padding(start = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ){
                    Text(
                        expertise.cost,
                        style = MaterialTheme.typography.bodySmall
                    )
                    var difficulty = expertise.difficultType+" / "+expertise.difficultLevel
                    if(expertise.difficultExtra.isNotEmpty()) difficulty += " - "+expertise.difficultExtra
                    Text(
                        difficulty,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = expertise.nh,
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.End
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpertiseCardPreview() {
    KingdomTheme {
        ExpertiseCard(SyrioAugustoModel.model.expertise.list[17])
    }
}