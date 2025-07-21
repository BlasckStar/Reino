package com.blasck.reino.framework.di

import com.blasck.reino.data.datasource.KingdomDataSource
import com.blasck.reino.data.repository.KingdomRepositoryImpl
import com.blasck.reino.domain.repository.KingdomRepository
import com.blasck.reino.domain.usecase.GetCharacterListByTypeUseCase
import com.blasck.reino.domain.usecase.GetCharacterListByTypeUseCaseImpl
import com.blasck.reino.framework.datasource.KingdomDataSourceImpl
import com.blasck.reino.presentation.viewmodel.ServiceViewModel
import org.koin.dsl.module
import com.blasck.reino.presentation.viewmodel.controllers.ToolbarController
import org.koin.core.context.GlobalContext.unloadKoinModules
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named

object KingdomModule {
    const val TOOLBAR = "Toolbar"
    const val SERVICE_VIEWMODEL = "ServiceViewModel"
    val kingdomModule = module {
        single { Connector.provideRetrofit() }
        factory<KingdomDataSource> { KingdomDataSourceImpl(get()) }
        factory<KingdomRepository> { KingdomRepositoryImpl(get()) }
        factory<GetCharacterListByTypeUseCase> { GetCharacterListByTypeUseCaseImpl(get()) }

        viewModel(named(TOOLBAR)) { ToolbarController() }
        viewModel(named(SERVICE_VIEWMODEL)) { ServiceViewModel(get()) }
    }

}