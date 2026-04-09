package com.dpkv.color_trading.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Routes {

    @Serializable
    object LoginScreen

    @Serializable
    object SignUpScreen

    @Serializable
    object GameScreen

    @Serializable
    object AccountScreen

    @Serializable
    object HistoryScreen
}

sealed class SubNavigation {
    @Serializable
    object AuthRoutes

    @Serializable
    object HomeRoutes

}