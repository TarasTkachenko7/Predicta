package com.predicta.app.ui.theme

import androidx.compose.ui.graphics.Color
val PrimaryBlue = Color(0xFF012D5A)
val PrimaryBlueLight = Color(0xFF1A4A7A)
val PrimaryBlueDark = Color(0xFF001A3A)

val SecondarySlate = Color(0xFF656766)
val SecondarySlateLight = Color(0xFF8A8C8B)
val SecondarySlateDark = Color(0xFF4A4C4B)

val SurfaceWhite = Color(0xFFFFFFFF)
val BackgroundWhite = Color(0xFFFFFFFF)
val BackgroundOffWhite = Color(0xFFF8F9FA)

val TextPrimary = Color(0xFF1A1A1A)
val TextSecondary = Color(0xFF656766)
val TextOnPrimary = Color(0xFFFFFFFF)

val DividerColor = Color(0xFFE8E8E8)
val ErrorRed = Color(0xFFD32F2F)
val SuccessGreen = Color(0xFF2E7D32)
val WarningAmber = Color(0xFFF9A825)
val SemanticCritical = Color(0xFFE63946)
val SemanticWarning = Color(0xFFF5A623)
val SemanticSuccess = Color(0xFF2EA043)

val BackgroundCritical = Color(0xFFFDECEA)
val BackgroundWarning = Color(0xFFFEF6E9)
val BackgroundSuccess = Color(0xFFEAF5EC)

enum class BurnoutLevel {
    LOW, MEDIUM, HIGH;

    fun getStrokeColor(): Color = when (this) {
        LOW -> SemanticSuccess
        MEDIUM -> SemanticWarning
        HIGH -> SemanticCritical
    }

    fun getBackgroundColor(): Color = when (this) {
        LOW -> BackgroundSuccess
        MEDIUM -> BackgroundWarning
        HIGH -> BackgroundCritical
    }

    companion object {
        fun fromRisk(risk: Float): BurnoutLevel {
            val percentage = risk * 100
            return when {
                percentage >= 80f -> HIGH
                percentage >= 50f -> MEDIUM
                else -> LOW
            }
        }
    }
}

