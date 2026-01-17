package com.example.quest.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = TextOnDark,
    primaryContainer = LightGreen,
    onPrimaryContainer = TextDark,
    secondary = SunflowerYellow,
    onSecondary = TextDark,
    secondaryContainer = LightYellow,
    onSecondaryContainer = TextDark,
    tertiary = SkyBlue,
    onTertiary = TextOnDark,
    tertiaryContainer = LightBlue,
    onTertiaryContainer = TextDark,
    background = CreamBackground,
    onBackground = TextDark,
    surface = CardWhite,
    onSurface = TextDark,
    surfaceVariant = LightGray,
    onSurfaceVariant = TextMedium,
    outline = BorderMedium,
    outlineVariant = BorderDark,
    error = CoralRed,
    onError = TextOnDark,
    errorContainer = LightCoral,
    onErrorContainer = TextDark
)

private val DarkColorScheme = darkColorScheme(
    primary = SageGreen,
    onPrimary = TextDark,
    primaryContainer = ForestGreen,
    onPrimaryContainer = TextOnDark,
    secondary = SunflowerYellow,
    onSecondary = TextDark,
    background = TextDark,
    onBackground = TextOnDark,
    surface = BorderDark,
    onSurface = TextOnDark,
    error = CoralRed,
    onError = TextOnDark
)

@Composable
fun QuestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}