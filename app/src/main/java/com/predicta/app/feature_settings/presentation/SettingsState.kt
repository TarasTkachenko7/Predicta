package com.predicta.app.feature_settings.presentation

import com.predicta.app.feature_settings.domain.model.ThemeMode

data class SettingsState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)
