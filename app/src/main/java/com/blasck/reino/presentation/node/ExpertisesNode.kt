package com.blasck.reino.presentation.node

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.blasck.reino.presentation.components.card.ExpertiseCard
import com.blasck.reino.presentation.screen.model.CharacterModel

@Composable
fun ExpertisesNode(expertise: CharacterModel.Expertise) {
    LazyColumn {
        items(expertise.list){
            ExpertiseCard(it)
        }
    }
}