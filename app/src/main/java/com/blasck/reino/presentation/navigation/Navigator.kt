package com.blasck.reino.presentation.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blasck.reino.R
import com.blasck.reino.presentation.components.KingdomToolbar
import com.blasck.reino.presentation.screen.CharacterScreen
import com.blasck.reino.presentation.screen.HomeScreen
import com.blasck.reino.presentation.state.ToolbarState
import com.blasck.reino.presentation.utils.MENU_DEDICATED
import com.blasck.reino.presentation.utils.MENU_HOME
import com.blasck.reino.presentation.viewmodel.controllers.ToolbarController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigator(
    toolbarController: ToolbarController,
    onFinish : () -> Unit,
) {
    val navController = rememberNavController()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val toolbarState by toolbarController.toolbarState.collectAsState()

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            KingdomToolbar(
                state = toolbarState,
                scrollBehavior = scrollBehavior,
                onBackAction = {
                    toolbarState.let{
                        when(it){
                            is ToolbarState.Home -> { onFinish() }
                            is ToolbarState.CanEdit -> { navController.popBackStack() }
                            is ToolbarState.OnlyTitle -> { navController.popBackStack() }
                            is ToolbarState.Editing -> { }
                        }
                    }
                },
                onExtraAction = { }
                )
        }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = MENU_HOME,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
            ){
                composable(MENU_HOME) {
                    HomeScreen(
                        callModal = { onResult -> onResult(true) },
                        navigateTo = {
                            Log.e("TesteLuiz","teste")
                            navController.navigate(it)
                        }
                    )
                    toolbarController.updateToolbarState(ToolbarState.Home())
                }
                composable(MENU_DEDICATED){
                    CharacterScreen()
                    toolbarController.updateToolbarState(ToolbarState.CanEdit(
                        title = "Dedicados",
                    ))
                }
            }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun NavigatorPreview() {
    Navigator(ToolbarController()){}
}