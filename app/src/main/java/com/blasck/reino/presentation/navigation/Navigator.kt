package com.blasck.reino.presentation.navigation

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
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.blasck.reino.presentation.components.KingdomToolbar
import com.blasck.reino.presentation.enums.CharacterListFilters
import com.blasck.reino.presentation.enums.HomeScreens
import com.blasck.reino.presentation.screen.AppScreens
import com.blasck.reino.presentation.screen.CharacterImportScreen
import com.blasck.reino.presentation.screen.CharacterScreen
import com.blasck.reino.presentation.screen.CharacterSessionScreen
import com.blasck.reino.presentation.screen.CharacterSearchScreen
import com.blasck.reino.presentation.screen.CustomDriveSourceScreen
import com.blasck.reino.presentation.screen.DriveCatalogScreen
import com.blasck.reino.presentation.screen.ErrorScreen
import com.blasck.reino.presentation.screen.HomeScreen
import com.blasck.reino.presentation.screen.LocalCharacterListScreen
import com.blasck.reino.presentation.state.ToolbarState
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
                            navController.navigate(screen.toAppScreen())
                        }
                    )
                    updateToolbar(ToolbarState.Home())
                }

                composable<AppScreens.CHARACTER_IMPORT> {
                    CharacterImportScreen(
                        onSaved = { navController.popBackStack() },
                        onBack = { navController.popBackStack() },
                    )
                    updateToolbar(ToolbarState.OnlyTitle("Importar ficha"))
                }

                composable<AppScreens.DRIVE_CATALOG> {
                    DriveCatalogScreen(
                        onImported = { id, name ->
                            navController.popBackStack()
                        },
                        onOpenCharacter = { id, name ->
                            navController.navigate(AppScreens.LocalCharacterView(id, name))
                        },
                        title = "Personagens no Drive",
                    )
                    updateToolbar(ToolbarState.OnlyTitle("Personagens no Drive"))
                }

                composable<AppScreens.CUSTOM_DRIVE_SOURCE> {
                    CustomDriveSourceScreen()
                    updateToolbar(ToolbarState.OnlyTitle("Fonte de importacao"))
                }

                composable<AppScreens.CharacterSession> { backStackEntry ->
                    val screen: AppScreens.CharacterSession = backStackEntry.toRoute()
                    CharacterSessionScreen(characterId = screen.id)
                    updateToolbar(ToolbarState.OnlyTitle(screen.name))
                }

                composable<AppScreens.LocalCharacterView> { backStackEntry ->
                    val screen: AppScreens.LocalCharacterView = backStackEntry.toRoute()
                    CharacterScreen(
                        id = screen.id,
                        onEditSession = {
                            navController.navigate(
                                AppScreens.CharacterSession(screen.id, screen.name),
                            )
                        },
                        onSearch = {
                            navController.navigate(
                                AppScreens.CharacterSearch(screen.id, screen.name),
                            )
                        },
                        onUpdateSheet = {
                            navController.navigate(
                                AppScreens.CharacterUpdate(screen.id, screen.name),
                            )
                        },
                    )
                    updateToolbar(ToolbarState.OnlyTitle(screen.name))
                }

                composable<AppScreens.CharacterUpdate> { backStackEntry ->
                    val screen: AppScreens.CharacterUpdate = backStackEntry.toRoute()
                    CharacterImportScreen(
                        updateCharacterId = screen.id,
                        onSaved = { navController.popBackStack() },
                        onBack = { navController.popBackStack() },
                    )
                    updateToolbar(ToolbarState.OnlyTitle("Atualizar ${screen.name}"))
                }

                composable<AppScreens.CharacterSearch> { backStackEntry ->
                    val screen: AppScreens.CharacterSearch = backStackEntry.toRoute()
                    CharacterSearchScreen(characterId = screen.id)
                    updateToolbar(ToolbarState.OnlyTitle("Buscar em ${screen.name}"))
                }

                composable<AppScreens.CharacterList> { backStackEntry ->
                    val screen: AppScreens.CharacterList = backStackEntry.toRoute()
                    if (screen.filter == CharacterListFilters.DEDICATED.value) {
                        LocalCharacterListScreen(
                            onImportCharacter = {
                                navController.navigate(AppScreens.DRIVE_CATALOG)
                            },
                            onOpenCharacter = { id, name ->
                                navController.navigate(AppScreens.LocalCharacterView(id, name))
                            },
                        )
                    }
                    updateToolbar(ToolbarState.OnlyTitle(screen.screen))
                }
                composable<AppScreens.Error>{ backEntry ->
                    val screen: AppScreens.Error = backEntry.toRoute()
                    ErrorScreen(screen.error, screen.code)
                    updateToolbar(ToolbarState.OnlyTitle("Aviso"))
                }
            }
    }
}



internal fun HomeScreens.toAppScreen(): AppScreens =
    when (this) {
        HomeScreens.IMPORT_CHARACTER -> AppScreens.DRIVE_CATALOG
        HomeScreens.DRIVE_CATALOG -> AppScreens.DRIVE_CATALOG
        HomeScreens.CUSTOM_DRIVE_SOURCE -> AppScreens.CUSTOM_DRIVE_SOURCE
        HomeScreens.DEDICATED ->
            AppScreens.CharacterList(
                filter = CharacterListFilters.DEDICATED.value,
                screen = HomeScreens.DEDICATED.title,
            )
    }
