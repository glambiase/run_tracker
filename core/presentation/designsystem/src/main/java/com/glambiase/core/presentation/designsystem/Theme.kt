package com.glambiase.core.presentation.designsystem

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val DarkColorScheme = darkColorScheme(
    primary = RunTrackerGreen,
    background = RunTrackerBlack,
    surface = RunTrackerDarkGray,
    secondary = RunTrackerWhite,
    tertiary = RunTrackerWhite,
    primaryContainer = RunTrackerGreen5,
    onPrimary = RunTrackerBlack,
    onBackground = RunTrackerWhite,
    onSurface = RunTrackerWhite,
    onSurfaceVariant = RunTrackerGray,
    error = RunTrackerDarkRed,
    errorContainer = RunTrackerDarkRed5
)

@Composable
fun RunTrackerTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}