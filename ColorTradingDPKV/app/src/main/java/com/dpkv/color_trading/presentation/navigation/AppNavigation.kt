package com.dpkv.color_trading.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHost
import androidx.navigation.compose.*
import com.dpkv.color_trading.datastore.local.TokenManager
import com.dpkv.color_trading.network.SessionManager
import com.dpkv.color_trading.presentation.screens.account.AccountScreenUI
import com.dpkv.color_trading.presentation.screens.authScreens.LoginScreenUI
import com.dpkv.color_trading.presentation.screens.authScreens.SignUpScreenUI
import com.dpkv.color_trading.presentation.screens.history.HistoryScreenUI
import com.dpkv.color_trading.presentation.screens.homescreen.GameScreenUI
import com.dpkv.color_trading.presentation.viewmodels.authviewmodel.AuthViewModel
import com.dpkv.color_trading.presentation.viewmodels.gameviewmodel.GameViewModel
import com.dpkv.color_trading.presentation.viewmodels.rootViewModel.RootViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = hiltViewModel(),
    gameViewModel: GameViewModel = hiltViewModel(),
    sessionManager: SessionManager,
    tokenManager: TokenManager,
    rootViewModel: RootViewModel = hiltViewModel()
) {

    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val token by tokenManager.accessToken.collectAsState(initial = null)
    val isLoggedIn by rootViewModel.isLoggedIn.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        BottomNavItem("Game", Icons.Default.Home, Routes.GameScreen),
        BottomNavItem("History", Icons.Default.History, Routes.HistoryScreen),
        BottomNavItem("Account", Icons.Default.Person, Routes.AccountScreen)
    )

    val showBottomBar = items.any { item ->
        currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
    }



    val startScreen = if (isLoggedIn == true) {
        SubNavigation.HomeRoutes
    } else {
        SubNavigation.AuthRoutes
    }

    if (isLoggedIn == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading...")
        }
        return
    }

    LaunchedEffect(Unit) {
        sessionManager.logoutEvent.collect {
            navController.navigate(Routes.LoginScreen) {
                popUpTo(0)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
        ),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 8.dp
                ) {
                    items.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any {
                            it.hasRoute(item.route::class)
                        } == true
                        
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = startScreen,
            modifier = Modifier.padding(paddingValues)
        ) {

            navigation<SubNavigation.AuthRoutes>(
                startDestination = Routes.LoginScreen
            ) {
                composable<Routes.LoginScreen> {
                    LoginScreenUI(
                        authViewModel,
                        onSignUpButtonClick = {
                            navController.navigate(Routes.SignUpScreen)
                        },
                        onLoginSuccessful = {
                            navController.navigate(SubNavigation.HomeRoutes) {
                                popUpTo(SubNavigation.AuthRoutes) { inclusive = true }
                            }
                        }
                    )
                }

                composable<Routes.SignUpScreen> {
                    SignUpScreenUI(
                        authViewModel,
                        onLoginButtonClick = {
                            navController.navigate(Routes.LoginScreen)
                        },
                        onSignUpSuccess = {
                            navController.navigate(Routes.LoginScreen)
                        }
                    )
                }
            }

            navigation<SubNavigation.HomeRoutes>(
                startDestination = Routes.GameScreen
            ) {
                composable<Routes.GameScreen> {
                    GameScreenUI(gameViewModel, snackbarHostState = snackbarHostState)
                }
                composable<Routes.HistoryScreen> {
                    HistoryScreenUI()
                }
                composable<Routes.AccountScreen> {
                    AccountScreenUI()
                }
            }
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: Any
)

private fun NavDestination.hasRoute(route: kotlin.reflect.KClass<*>): Boolean {
    return route.qualifiedName?.let { name ->
        this.route?.contains(name) == true
    } ?: false
}