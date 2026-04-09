package com.dpkv.color_trading.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.dpkv.color_trading.network.SessionManager
import com.dpkv.color_trading.presentation.screens.authScreens.LoginScreenUI
import com.dpkv.color_trading.presentation.screens.authScreens.SignUpScreenUI
import com.dpkv.color_trading.presentation.screens.homescreen.GameScreenUI
import com.dpkv.color_trading.presentation.viewmodels.authviewmodel.AuthViewModel
import com.dpkv.color_trading.presentation.viewmodels.gameviewmodel.GameViewModel

@Composable
fun AppNavigation(
    authViewModel : AuthViewModel = hiltViewModel(),
    gameViewModel: GameViewModel = hiltViewModel<GameViewModel>(),
    sessionManager: SessionManager
){

    val navController = rememberNavController()
    val startScreen = SubNavigation.AuthRoutes

    LaunchedEffect(Unit) {
        sessionManager.logoutEvent.collect {

            navController.navigate(Routes.LoginScreen) {
                popUpTo(0)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        NavHost(
            navController=navController,
            startDestination = startScreen
        ) {
            navigation<SubNavigation.AuthRoutes>(
                startDestination = Routes.LoginScreen
            ){
                composable<Routes.LoginScreen> {
                    LoginScreenUI(authViewModel, onSignUpButtonClick = {
                        navController.navigate(Routes.SignUpScreen)

                    }, onLoginSuccessful = {
                        navController.navigate(Routes.GameScreen)
                    })

                }
                composable<Routes.SignUpScreen> {
                    SignUpScreenUI(authViewModel, onLoginButtonClick = {
                        navController.navigate(Routes.LoginScreen)
                    }, onSignUpSuccess = {
                        navController.navigate(Routes.LoginScreen)
                    })
                }

            }

            navigation<SubNavigation.HomeRoutes>(
                startDestination = Routes.GameScreen
            ){
                composable<Routes.GameScreen> {
                    GameScreenUI(gameViewModel)

                }

            }

        }
    }



}