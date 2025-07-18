package com.blasck.reino.framework.di

import com.blasck.reino.presentation.viewmodel.controllers.ToolbarController
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.loadKoinModules
import org.koin.core.qualifier.named
import org.koin.dsl.module

object KingdomModule {
    const val TOOLBAR = "Toolbar"
    val kingdomModule = module{
        viewModel(named(TOOLBAR)){
            ToolbarController()
        }
    }

    fun loadKingdomModule() = loadKoinModules(kingdomModule)
    fun unloadKingdomModule() = loadKoinModules(kingdomModule)
}