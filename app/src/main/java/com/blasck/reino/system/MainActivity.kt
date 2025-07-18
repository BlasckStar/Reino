package com.blasck.reino.system

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
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
import com.blasck.reino.framework.di.KingdomModule.unloadKingdomModule
import com.blasck.reino.presentation.navigation.Navigator
import com.blasck.reino.presentation.viewmodel.controllers.ToolbarController
import com.blasck.reino.system.theme.KingdomTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class MainActivity : ComponentActivity() {

    private val toolbarController: ToolbarController by viewModel(named(TOOLBAR))

    @SuppressLint("SourceLockedOrientationActivity")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
        setContent {
            KingdomTheme {
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
