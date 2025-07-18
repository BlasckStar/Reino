package com.blasck.reino.presentation.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.blasck.reino.R
import com.blasck.reino.presentation.components.IconNamedButton
import com.blasck.reino.presentation.utils.MENU_GM
import com.blasck.reino.presentation.utils.MENU_POLL
import com.blasck.reino.presentation.utils.MENU_WIKI

@Composable
fun HomeScreen(
    callModal: (onResult: (Boolean) -> Unit) -> Unit,
    navigateTo: (String) -> Unit
){

    Column {
        //Image Map of Reino TODO: Montar no back a imagem do reino para ser atualizada pelo mestre
        // Dedicated Characters
        IconNamedButton("Dedicados", R.drawable.ic_btn_dedicated) {
//            navigateTo(MENU_DEDICATED)
            navigateTo("Character")
        }
        // Poll
        IconNamedButton("Poll", R.drawable.ic_btn_pool) { navigateTo(MENU_POLL) }
        // GM
        IconNamedButton(title = "Mestre", R.drawable.ic_btn_master) { navigateTo(MENU_GM) }
        // WIKI
        IconNamedButton(title = "Reinopedia", R.drawable.ic_btn_wiki) { callModal{ result ->
            if(result) Log.e("TesteLuiz", "Passou essa krai")
        } }
    }

}
