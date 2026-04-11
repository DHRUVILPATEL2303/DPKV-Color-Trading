package com.dpkv.color_trading_admin_dpkv.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.combine

import com.dpkv.color_trading_admin_dpkv.presentation.screens.dashboard.DashboardScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Routes.Dashboard
    ) {
        composable<Routes.Dashboard> {
            DashboardScreen()
        }
    }
}