package com.blasck.reino.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.blasck.reino.presentation.layout.HomeLayout

@Composable
fun HomeScreen(
    navigateTo: (String) -> Unit
){
    Column {
        HomeLayout { navigateTo(it) }
    }
}
