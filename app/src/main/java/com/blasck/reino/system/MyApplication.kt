package com.blasck.reino.system

import android.app.Application
import com.blasck.reino.domain.repository.DefaultDriveSourceProvider
import com.blasck.reino.framework.di.KingdomModule.kingdomModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class MyApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        val koinApplication =
            startKoin {
                androidLogger()
                androidContext(this@MyApplication)
                modules(listOf(kingdomModule))
            }
        applicationScope.launch {
            koinApplication.koin.get<DefaultDriveSourceProvider>().refresh()
        }
    }
}
