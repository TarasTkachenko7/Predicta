package com.predicta.app.feature_settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predicta.app.core.network.PredictaBaseUrlProvider
import com.predicta.app.feature_auth.data.session.UserSessionManager
import com.predicta.app.feature_settings.data.repository.AppSettingsRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: AppSettingsRepository,
    private val sessionManager: UserSessionManager,
    private val baseUrlProvider: PredictaBaseUrlProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                settingsRepository.settings,
                sessionManager.session,
                baseUrlProvider.baseUrl,
            ) { appSettings, session, apiBaseUrl ->
                SettingsState(
                    themeMode = appSettings.themeMode,
                    apiBaseUrl = apiBaseUrl,
                    userName = session.userName,
                    email = session.email,
                    role = session.role,
                    isLoggedIn = session.isLoggedIn,
                    avatarUri = session.avatarUri,
                )
            }.collect { settingsState ->
                _state.update {
                    settingsState
                }
            }
        }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ChangeTheme -> {
                settingsRepository.setThemeMode(event.themeMode)
            }
            is SettingsEvent.UpdateApiBaseUrl -> {
                baseUrlProvider.setBaseUrl(event.url)
            }
            SettingsEvent.ResetApiBaseUrl -> {
                baseUrlProvider.resetBaseUrl()
            }
            is SettingsEvent.UpdateName -> {
                sessionManager.updateName(event.name)
            }
            is SettingsEvent.UpdateAvatar -> {
                sessionManager.updateAvatar(event.uri)
            }
            SettingsEvent.Logout -> {
                sessionManager.clearSession()
            }
        }
    }
}
