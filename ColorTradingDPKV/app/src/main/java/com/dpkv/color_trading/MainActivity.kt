package com.dpkv.color_trading

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dpkv.color_trading.datastore.local.TokenManager
import com.dpkv.color_trading.network.SessionManager
import com.dpkv.color_trading.presentation.navigation.AppNavigation
import com.dpkv.color_trading.ui.theme.ColorTradingDPKVTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ColorTradingDPKVTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    AppNavigation(sessionManager =sessionManager, tokenManager = tokenManager)
                }
            }
        }
    }
}

