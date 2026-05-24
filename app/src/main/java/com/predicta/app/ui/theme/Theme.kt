package com.predicta.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.predicta.app.feature_settings.domain.model.ThemeMode

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

private val PredictaDarkColorScheme = darkColorScheme(
    primary = Color(0xFF8FC7FF),
    onPrimary = Color(0xFF00294F),
    primaryContainer = Color(0xFF0D3A67),
    onPrimaryContainer = Color(0xFFD7E9FF),
    secondary = Color(0xFFC7C9C8),
    onSecondary = Color(0xFF2F3130),
    secondaryContainer = Color(0xFF3F4448),
    onSecondaryContainer = Color(0xFFE1E3E2),
    background = Color(0xFF0E1116),
    onBackground = Color(0xFFE7EAEE),
    surface = Color(0xFF171B22),
    onSurface = Color(0xFFE7EAEE),
    surfaceVariant = Color(0xFF222832),
    onSurfaceVariant = Color(0xFFB8C0CC),
    error = ErrorRed,
    onError = TextOnPrimary,
    outline = Color(0xFF3A4350),
    outlineVariant = Color(0xFF2A303A),
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
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val colorScheme = if (darkTheme) PredictaDarkColorScheme else PredictaLightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PredictaTypography,
        content = content,
    )
}
