package com.trustlens.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TrustLensDarkColorScheme = darkColorScheme(
    primary = TrustCyan,
    onPrimary = TrustBlueDark,
    primaryContainer = TrustBlueLight,
    onPrimaryContainer = TextPrimary,
    secondary = TrustBlueLight,
    onSecondary = TextPrimary,
    background = SurfaceDark,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = TextDisabled,
    error = ScoreLow,
    onError = White
)

@Composable
fun TrustLensTheme(content: @Composable () -> Unit) {
    val colorScheme = TrustLensDarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = SurfaceDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TrustLensTypography,
        content = content
    )
}