package com.blasck.reino.presentation.node

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.blasck.reino.presentation.components.card.ExpertiseCard
import com.blasck.reino.presentation.screen.model.CharacterModel

@Composable
fun ExpertisesNode(expertise: CharacterModel.Expertise) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        expertise.list.forEach {
            ExpertiseCard(it)
        }
    }
}
