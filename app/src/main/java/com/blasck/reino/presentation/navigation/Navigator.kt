package com.blasck.reino.presentation.navigation

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.blasck.reino.presentation.components.KingdomToolbar
import com.blasck.reino.presentation.enums.CharacterListFilters
import com.blasck.reino.presentation.enums.HomeScreens
import com.blasck.reino.presentation.screen.AppScreens
import com.blasck.reino.presentation.screen.CharacterListScreen
import com.blasck.reino.presentation.screen.CharacterScreen
import com.blasck.reino.presentation.screen.ErrorScreen
import com.blasck.reino.presentation.screen.HomeScreen
import com.blasck.reino.presentation.screen.WikiScreen
import com.blasck.reino.presentation.state.ToolbarState
import com.blasck.reino.presentation.viewmodel.ServiceViewModel
import com.blasck.reino.presentation.viewmodel.controllers.ToolbarController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigator(
    toolbarController: ToolbarController,
    service: ServiceViewModel,
    onFinish : () -> Unit,
) {

    val navController = rememberNavController()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val toolbarState by toolbarController.toolbarState.collectAsState()

    fun updateToolbar(state: ToolbarState){
        toolbarController.updateToolbarState(state)
    }

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
                            is ToolbarState.Editing -> { navController.popBackStack() }
                            is ToolbarState.Error -> { navController.navigate(AppScreens.HOME)}
                        }
                    }
                },
                onExtraAction = { }
                )
        }
        ) { innerPadding ->

            NavHost(
                navController = navController,
                startDestination = AppScreens.HOME,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ){
                composable<AppScreens.HOME> {
                    HomeScreen(
                        navigateTo = { screen ->
                            val navigateScreen = when(screen){
                                HomeScreens.DEDICATED -> {
                                    AppScreens.CharacterList(
                                        filter = CharacterListFilters.DEDICATED.value,
                                        screen = HomeScreens.DEDICATED.title
                                    )
                                }
                                HomeScreens.POLL -> {
                                    AppScreens.CharacterList(
                                        filter = CharacterListFilters.POLL.value,
                                        screen = HomeScreens.POLL.title
                                    )
                                }
                                HomeScreens.MASTER -> {
                                    AppScreens.CharacterList(
                                        filter = CharacterListFilters.MASTER.value,
                                        screen = HomeScreens.MASTER.title
                                    )
                                }
                                HomeScreens.WIKI -> { AppScreens.WIKI }
                            }
                            navController.navigate(navigateScreen)
                        }
                    )
                    updateToolbar(ToolbarState.Home())
                }

                composable<AppScreens.CharacterList> { backStackEntry ->
                    val screen: AppScreens.CharacterList = backStackEntry.toRoute()
                    CharacterListScreen(CharacterListFilters.DEDICATED,
                        services = service,
                        goToCharacter = { id, name ->
                            navController.navigate(AppScreens.CharacterView(id, name))
                        })
                    {
                        navController.navigate(AppScreens.Error("Deu merda"))
                    }
                    updateToolbar(ToolbarState.OnlyTitle(screen.screen))
                }
                composable<AppScreens.WIKI> {
                    WikiScreen()
                    updateToolbar(ToolbarState.OnlyTitle("Reinopedia"))
                }
                composable<AppScreens.Error>{ backEntry ->
                    val screen: AppScreens.Error = backEntry.toRoute()
                    ErrorScreen(screen.error, screen.code)
                    updateToolbar(ToolbarState.Error("Eu arco meu saco"))
                }
                composable<AppScreens.CharacterView> { backStackEntry ->
                    val screen: AppScreens.CharacterView = backStackEntry.toRoute()
                    CharacterScreen(
                        screen.id,
                        onEditing = {
                            Log.e("TesteLuiz", "onEditing")
                        },
                        toEditing = {
                            updateToolbar(ToolbarState.Editing())
                        },
                        onWaiting = {
                            updateToolbar(ToolbarState.CanEdit(screen.name))
                        }
                    )
                    updateToolbar(ToolbarState.CanEdit(screen.name))
                }

            }
    }
}
