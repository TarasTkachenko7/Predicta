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
    error = SemanticCritical,
    onError = TextOnPrimary,
    outline = DividerColor,
    outlineVariant = DividerColor,
)

private val PredictaDarkColorScheme = darkColorScheme(
    primary = Color(0xFF6CC4FF),
    onPrimary = Color(0xFF00243F),
    primaryContainer = Color(0xFF0C4B80),
    onPrimaryContainer = Color(0xFFE2F1FF),
    secondary = Color(0xFFD2E2F0),
    onSecondary = Color(0xFF24313D),
    secondaryContainer = Color(0xFF334355),
    onSecondaryContainer = Color(0xFFE8F2FB),
    background = Color(0xFF080C12),
    onBackground = Color(0xFFF0F5FA),
    surface = Color(0xFF111823),
    onSurface = Color(0xFFF0F5FA),
    surfaceVariant = Color(0xFF1A2430),
    onSurfaceVariant = Color(0xFFC2D2E0),
    error = SemanticCritical,
    onError = TextOnPrimary,
    outline = Color(0xFF536578),
    outlineVariant = Color(0xFF2D3946),
)


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

