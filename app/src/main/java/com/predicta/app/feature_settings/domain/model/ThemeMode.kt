package com.predicta.app.feature_settings.domain.model

import androidx.annotation.StringRes
import com.predicta.app.R

enum class ThemeMode(
    val storageValue: String,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
) {
    SYSTEM(
        storageValue = "system",
        titleRes = R.string.theme_mode_system_title,
        descriptionRes = R.string.theme_mode_system_description,
    ),
    LIGHT(
        storageValue = "light",
        titleRes = R.string.theme_mode_light_title,
        descriptionRes = R.string.theme_mode_light_description,
    ),
    DARK(
        storageValue = "dark",
        titleRes = R.string.theme_mode_dark_title,
        descriptionRes = R.string.theme_mode_dark_description,
    );

    companion object {
        fun fromStorageValue(value: String?): ThemeMode {
            return entries.firstOrNull { it.storageValue == value } ?: SYSTEM
        }
    }
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)
