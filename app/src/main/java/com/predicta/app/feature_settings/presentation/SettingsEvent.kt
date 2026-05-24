package com.predicta.app.feature_settings.presentation

import com.predicta.app.feature_settings.domain.model.ThemeMode

sealed interface SettingsEvent {
    data class ChangeTheme(val themeMode: ThemeMode) : SettingsEvent
    data object Logout : SettingsEvent
}
