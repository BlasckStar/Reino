package com.blasck.reino.framework.di

import androidx.room.Room
import com.blasck.reino.data.importer.XlsxCharacterSheetImporter
import com.blasck.reino.data.local.ReinoDatabase
import com.blasck.reino.data.local.MIGRATION_1_2
import com.blasck.reino.data.local.MIGRATION_2_3
import com.blasck.reino.data.local.MIGRATION_3_4
import com.blasck.reino.data.local.MIGRATION_4_5
import com.google.gson.Gson
import com.blasck.reino.data.repository.DriveFolderListingDataSource
import com.blasck.reino.data.repository.FallbackDriveFolderListingDataSource
import com.blasck.reino.data.repository.FirebaseRemoteConfigDriveSourceProvider
import com.blasck.reino.data.repository.GoogleDriveCatalogRepository
import com.blasck.reino.data.repository.LocalCharacterRepository
import com.blasck.reino.data.repository.LocalDriveImageStorageRepository
import com.blasck.reino.data.repository.PublicDriveFolderListingDataSource
import com.blasck.reino.data.repository.SharedPreferencesDriveSourceRepository
import com.blasck.reino.domain.repository.CharacterRepository
import com.blasck.reino.domain.repository.DefaultDriveSourceProvider
import com.blasck.reino.domain.repository.DriveCatalogRepository
import com.blasck.reino.domain.repository.DriveImageStorageRepository
import com.blasck.reino.domain.repository.DriveSourceRepository
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.blasck.reino.presentation.viewmodel.CharacterImportViewModel
import com.blasck.reino.presentation.viewmodel.HomeViewModel
import com.blasck.reino.presentation.viewmodel.CharacterSessionViewModel
import com.blasck.reino.presentation.viewmodel.CharacterViewModel
import com.blasck.reino.presentation.viewmodel.CharacterSearchViewModel
import com.blasck.reino.presentation.viewmodel.CustomDriveSourceViewModel
import com.blasck.reino.presentation.viewmodel.DriveCatalogViewModel
import okhttp3.OkHttpClient
import org.koin.dsl.module
import com.blasck.reino.presentation.viewmodel.controllers.ToolbarController
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named

object KingdomModule {
    const val TOOLBAR = "Toolbar"
    val kingdomModule = module {
        single {
            Room.databaseBuilder(
                get(),
                ReinoDatabase::class.java,
                "reino.db",
            ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5).build()
        }
        single { get<ReinoDatabase>().characterDao() }
        single { get<ReinoDatabase>().expertiseDao() }
        single { get<ReinoDatabase>().characterBackupDao() }
        single { Gson() }
        single { FirebaseRemoteConfig.getInstance() }
        single { OkHttpClient() }
        single<CharacterRepository> { LocalCharacterRepository(get(), get(), get(), get(), get()) }
        single<DriveFolderListingDataSource> {
            FallbackDriveFolderListingDataSource(
                primary = PublicDriveFolderListingDataSource(get()),
            )
        }
        single<DefaultDriveSourceProvider> { FirebaseRemoteConfigDriveSourceProvider(get()) }
        single<DriveSourceRepository> { SharedPreferencesDriveSourceRepository(get(), get()) }
        single<DriveCatalogRepository> { GoogleDriveCatalogRepository(get(), get(), get()) }
        single<DriveImageStorageRepository> { LocalDriveImageStorageRepository(get()) }
        factory { XlsxCharacterSheetImporter() }

        viewModel(named(TOOLBAR)) { ToolbarController() }
        viewModel { CharacterImportViewModel(get(), get()) }
        viewModel { HomeViewModel(get()) }
        viewModel { CharacterSessionViewModel(get()) }
        viewModel { CharacterViewModel(get()) }
        viewModel { CharacterSearchViewModel(get()) }
        viewModel { CustomDriveSourceViewModel(get(), get()) }
        viewModel { DriveCatalogViewModel(get(), get(), get(), get()) }
    }

}
