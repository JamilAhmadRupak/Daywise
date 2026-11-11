package com.daywise.app.ui.theme

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

private val DaywiseColorScheme = darkColorScheme(
    primary = CyanPrimary,
    onPrimary = LeafDeepNavy,
    primaryContainer = HeroCardContainerDark,
    onPrimaryContainer = LeafIceWhite,
    secondary = CyanAccent,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onBackground = LeafIceWhite,
    onSurface = LeafIceWhite,
    onSurfaceVariant = LeafPeriwinkle,
    error = ErrorRed,
    outline = BorderDark
)

@Composable
fun DaywiseTheme(
    darkTheme: Boolean = true, // Daywise is designed as an immersive dark aesthetic; keeps contrast pristine
    content: @Composable () -> Unit
) {
    // Always use DaywiseColorScheme to prevent system light mode from inverting text to black on dark cards
    val colorScheme = DaywiseColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = false
            insetsController.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
