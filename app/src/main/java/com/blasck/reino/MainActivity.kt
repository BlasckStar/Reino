package com.blasck.reino

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.blasck.reino.framework.di.KingdomModule.TOOLBAR
import com.blasck.reino.framework.di.KingdomModule.kingdomModule
import com.blasck.reino.framework.di.KingdomModule.loadKingdomModule
import com.blasck.reino.framework.di.KingdomModule.unloadKingdomModule
import com.blasck.reino.presentation.navigation.Navigator
import com.blasck.reino.presentation.viewmodel.controllers.ToolbarController
import com.blasck.reino.ui.theme.ReinoTheme
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named

class MainActivity : ComponentActivity() {
    init {
        startKoin{
            androidLogger()
            androidContext(this@MainActivity)
            modules(kingdomModule)
        }
        loadKingdomModule()
    }

    override fun onDestroy() {
        super.onDestroy()
        unloadKingdomModule()
    }

    private val toolbarController: ToolbarController by inject<ToolbarController>(
        named(TOOLBAR)
    )

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            ReinoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    Navigator(toolbarController) {
                        finish()
                    }
                }

            }
        }
    }
}
