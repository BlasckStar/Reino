package com.blasck.reino.framework.di

import org.koin.dsl.module
import com.blasck.reino.presentation.viewmodel.controllers.ToolbarController
import org.koin.core.context.GlobalContext.loadKoinModules
import org.koin.core.context.GlobalContext.unloadKoinModules
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named

object KingdomModule {
    const val TOOLBAR = "Toolbar"
    val kingdomModule = module {
        viewModel(named(KingdomModule.TOOLBAR)) { ToolbarController() }
    }
    fun unloadKingdomModule() = unloadKoinModules(kingdomModule)
}