package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SafariLightColorScheme = lightColorScheme(
    primary = JunglePrimary,
    onPrimary = JungleOnPrimary,
    primaryContainer = JunglePrimaryContainer,
    onPrimaryContainer = JungleOnPrimaryContainer,
    secondary = SafariGold,
    onSecondary = SafariOnGold,
    secondaryContainer = SafariGoldContainer,
    onSecondaryContainer = SafariOnGoldContainer,
    tertiary = AdventureOrange,
    onTertiary = JungleOnPrimary,
    tertiaryContainer = AdventureOrangeContainer,
    background = JungleBackground,
    surface = JungleSurface,
    surfaceVariant = JungleSurfaceVariant,
    outline = JungleOutline
)

private val SafariDarkColorScheme = darkColorScheme(
    primary = ParrotGreen,
    onPrimary = JungleOnPrimaryContainer,
    primaryContainer = JunglePrimary,
    onPrimaryContainer = JunglePrimaryContainer,
    secondary = BananaYellow,
    onSecondary = SafariOnGoldContainer,
    secondaryContainer = SafariGold,
    onSecondaryContainer = SafariGoldContainer,
    tertiary = AdventureOrange,
    background = Color(0xFF1B2E1E),
    surface = Color(0xFF223625),
    surfaceVariant = Color(0xFF2E4632)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our vibrant jungle palette for consistent playful adventure
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> SafariDarkColorScheme
        else -> SafariLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.primary.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
