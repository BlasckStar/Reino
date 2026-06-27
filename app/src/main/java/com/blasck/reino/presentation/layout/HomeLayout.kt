package com.blasck.reino.presentation.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.blasck.reino.R
import com.blasck.reino.presentation.components.IconNamedButton
import com.blasck.reino.presentation.enums.HomeScreens
import com.blasck.reino.system.theme.KingdomTheme

@Composable
fun HomeLayout(
    navigateTo: (HomeScreens) -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        HomeHeader()
        IconNamedButton("Lista de personagens", R.drawable.ic_btn_dedicated) { navigateTo(HomeScreens.DEDICATED) }
        IconNamedButton("Fonte de importacao", R.drawable.ic_btn_drive_link) { navigateTo(HomeScreens.CUSTOM_DRIVE_SOURCE) }
        IconNamedButton("Poll", R.drawable.ic_btn_pool, false) { navigateTo(HomeScreens.POLL) }
        IconNamedButton(title = "Mestre", R.drawable.ic_btn_master, false) { navigateTo(HomeScreens.MASTER) }
        IconNamedButton(title = "Reinopedia", R.drawable.ic_btn_wiki) { navigateTo(HomeScreens.WIKI) }
    }
}

@Composable
private fun HomeHeader() {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Icone do projeto",
                    modifier = Modifier.size(116.dp),
                )
                Text(
                    text = "O Reino",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = "Fichas, grimorios e aventuras",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeLayoutPreview() {
    KingdomTheme {
        HomeLayout(navigateTo = {})
    }
}
