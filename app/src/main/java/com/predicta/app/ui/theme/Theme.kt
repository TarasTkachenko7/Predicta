package com.predicta.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val PredictaLightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryBlueLight,
    onPrimaryContainer = TextOnPrimary,
    secondary = SecondarySlate,
    onSecondary = TextOnPrimary,
    secondaryContainer = SecondarySlateLight,
    onSecondaryContainer = TextPrimary,
    background = BackgroundWhite,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundOffWhite,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = TextOnPrimary,
    outline = DividerColor,
    outlineVariant = DividerColor,
)

/**
 * Custom shape definitions for the SDM Bank Design System.
 * iOS-like modernism aesthetic: rounded corners at 16dp and 24dp.
 */
object PredictaShapes {
    val medium = RoundedCornerShape(16.dp)
    val large = RoundedCornerShape(24.dp)
}

@Composable
fun PredictaTheme(
    content: @Composable () -> Unit,
) {
    val colorScheme = PredictaLightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = PrimaryBlue.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PredictaTypography,
        content = content,
    )
}
