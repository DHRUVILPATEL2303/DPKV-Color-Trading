package com.dpkv.color_trading.presentation.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.dpkv.color_trading.presentation.viewmodels.histroyViewModel.HistoryViewModel
import com.dpkv.color_trading.presentation.viewmodels.rootViewModel.RootViewModel
import com.dpkv.color_trading.ui.theme.DeepNavyBlue
import com.dpkv.color_trading.ui.theme.SkyBlue

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = hiltViewModel(),
    gameViewModel: GameViewModel = hiltViewModel(),
    historyViewModel: HistoryViewModel = hiltViewModel(),
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
            authViewModel.clearData()
            gameViewModel.clearData()
            historyViewModel.clearData()
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
                val isDark = isSystemInDarkTheme()
                val barColor = if (isDark) SkyBlue else DeepNavyBlue
                val contentColor = if (isDark) DeepNavyBlue else Color.White
                
                Surface(
                    modifier = Modifier
                        .padding(start = 24.dp, end = 24.dp, bottom = 2.dp)
                        .navigationBarsPadding()
                        .border(
                            width = 2.dp,
                            color = Color(168, 92, 50),
                            shape = RoundedCornerShape(32.dp)
                        ),
                    shape = RoundedCornerShape(32.dp),
                    color = barColor.copy(alpha = 0.9f),
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        contentColor = contentColor,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(64.dp)
                    ) {
                        items.forEach { item ->
                            val isSelected = currentDestination?.hierarchy?.any {
                                it.hasRoute(item.route::class)
                            } == true

                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = {
                                    if (isSelected){

                                        Text(
                                            text = item.label,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                            fontSize = 11.sp
                                        )
                                    }
                                },
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
                                    selectedIconColor = if (isDark) DeepNavyBlue else Color(
                                        240,
                                        243,
                                        245,
                                        255
                                    ),
                                    selectedTextColor = if (isDark) DeepNavyBlue else Color.White,
                                    unselectedIconColor = contentColor.copy(alpha = 0.3f),
                                    unselectedTextColor = contentColor.copy(alpha = 0.6f),
                                    indicatorColor = Color.Transparent
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = startScreen,
            modifier = Modifier.padding(paddingValues),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(400))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -1000 },
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(400))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(400))
            }
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
                    HistoryScreenUI(historyViewModel)
                }
                composable<Routes.AccountScreen> {
                    AccountScreenUI(authViewModel, sessionManager)
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