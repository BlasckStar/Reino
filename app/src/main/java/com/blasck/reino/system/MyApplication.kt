package com.blasck.reino.system

import android.app.Application
import com.blasck.reino.framework.di.KingdomModule.kingdomModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(listOf(
                kingdomModule
            ))
        }
    }
}