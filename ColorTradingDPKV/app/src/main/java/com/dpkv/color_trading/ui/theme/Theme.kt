package com.dpkv.color_trading.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryAccent,
    onPrimary = MidnightBlack,
    secondary = SecondaryAccent,
    onSecondary = OffWhite,
    tertiary = NeonGlowGreen,
    background = MidnightBlack,
    onBackground = OffWhite,
    surface = SlateSurface,
    onSurface = OffWhite,
    surfaceVariant = DarkBorder,
    onSurfaceVariant = SteelGray,
    outline = DarkBorder,
    error = TradingRed,
    onError = OffWhite
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryAccent,
    onPrimary = PrimaryBlack,
    secondary = SecondaryAccent,
    onSecondary = OffWhite,
    tertiary = NeonGlowGreen,
    background = SoftLightBackground,
    onBackground = PrimaryBlack,
    surface = SoftLightSurface,
    onSurface = PrimaryBlack,
    surfaceVariant = SoftLightBorder,
    onSurfaceVariant = SlateGray,
    outline = SoftLightBorder,
    error = TradingRed,
    onError = OffWhite
)

@Composable
fun ColorTradingDPKVTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Keep it static for consistency unless user asks otherwise
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}