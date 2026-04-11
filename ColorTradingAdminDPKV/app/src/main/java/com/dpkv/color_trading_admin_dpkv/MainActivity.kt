package com.dpkv.color_trading_admin_dpkv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.dpkv.color_trading_admin_dpkv.presentation.navigation.AppNavigation
import com.dpkv.color_trading_admin_dpkv.ui.theme.ColorTradingAdminDPKVTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ColorTradingAdminDPKVTheme {
                AppNavigation()
            }
        }
    }
}
