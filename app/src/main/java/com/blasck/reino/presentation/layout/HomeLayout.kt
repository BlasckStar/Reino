package com.blasck.reino.presentation.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blasck.reino.R
import com.blasck.reino.presentation.components.CoilImage
import com.blasck.reino.presentation.components.IconNamedButton
import com.blasck.reino.presentation.utils.MENU_GM
import com.blasck.reino.presentation.utils.MENU_POLL
import com.blasck.reino.presentation.utils.MENU_WIKI

@Composable
fun HomeLayout(
    navigateTo: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp, 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        //Image Map of Reino TODO: Montar no back a imagem do reino para ser atualizada pelo mestre
        CoilImage("https://static.wikia.nocookie.net/tsrd/images/4/42/Mapa_arton1.jpg/revision/latest?cb=20180911162044&path-prefix=pt-br")
        // Dedicated Characters
        IconNamedButton("Dedicados", R.drawable.ic_btn_dedicated) { navigateTo("Character") } //navigateTo(MENU_DEDICATED) }
        // Poll
        IconNamedButton("Poll", R.drawable.ic_btn_pool) { navigateTo(MENU_POLL) }
        // GM
        IconNamedButton(title = "Mestre", R.drawable.ic_btn_master) { navigateTo(MENU_GM) }
        // WIKI
        IconNamedButton(title = "Reinopedia", R.drawable.ic_btn_wiki) { navigateTo(MENU_WIKI) }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeLayoutPreview() {
    HomeLayout { }
}
