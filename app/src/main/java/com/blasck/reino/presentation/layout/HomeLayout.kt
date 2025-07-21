package com.blasck.reino.presentation.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blasck.reino.R
import com.blasck.reino.presentation.enums.HomeScreens
import com.blasck.reino.presentation.components.CoilImage
import com.blasck.reino.presentation.components.IconNamedButton
import com.blasck.reino.system.theme.KingdomTheme

@Composable
fun HomeLayout(
    navigateTo: (HomeScreens) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp, 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        //Image Map of Reino TODO: Montar no back a imagem do reino para ser atualizada pelo mestre
        CoilImage("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQmBqCH3lKNWDWO_faHUchfbeLrpt0U9Ncnog&s")
        // Dedicated Characters
        IconNamedButton("Dedicados", R.drawable.ic_btn_dedicated) { navigateTo(HomeScreens.DEDICATED) } //navigateTo(MENU_DEDICATED) }
        // Poll
        IconNamedButton("Poll", R.drawable.ic_btn_pool, false) { navigateTo(HomeScreens.POLL) }
        // GM
        IconNamedButton(title = "Mestre", R.drawable.ic_btn_master, false) { navigateTo(HomeScreens.MASTER) }
        // WIKI
        IconNamedButton(title = "Reinopedia", R.drawable.ic_btn_wiki) { navigateTo(HomeScreens.WIKI) }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeLayoutPreview() {
    KingdomTheme {
        HomeLayout { }
    }
}
