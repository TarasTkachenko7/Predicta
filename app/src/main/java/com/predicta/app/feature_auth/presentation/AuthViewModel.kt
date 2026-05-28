package com.predicta.app.feature_auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predicta.app.core.error.AppResult
import com.predicta.app.core.ui.UiEffect
import com.predicta.app.core.ui.toUiText
import com.predicta.app.feature_auth.data.session.UserSessionManager
import com.predicta.app.feature_auth.domain.usecase.AuthInteractors
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val interactors: AuthInteractors,
    private val sessionManager: UserSessionManager,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<AuthEffect>()
    val effects: SharedFlow<AuthEffect> = _effects.asSharedFlow()

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.EmailChanged,
            is AuthEvent.PasswordChanged,
            is AuthEvent.FirstNameChanged,
            is AuthEvent.LastNameChanged,
            is AuthEvent.TelegramNickChanged,
            is AuthEvent.PhoneChanged,
            AuthEvent.ResetSuccessState,
            -> _state.update { reduceAuthInput(it, event) }
            AuthEvent.LoginSubmit -> login()
            AuthEvent.RegisterSubmit -> register()
        }
    }

    private fun login() {
        val errors = validateLogin(_state.value)
        if (errors.isNotEmpty()) {
            _state.update { applyValidationErrors(it.copy(globalError = null), errors) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, globalError = null) }
            when (val result = interactors.login(_state.value.email, _state.value.password)) {
                is AppResult.Success -> {
                    sessionManager.startSession(result.value)
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                    _effects.emit(AuthEffect.Authenticated)
                }
                is AppResult.Failure -> {
                    _state.update {
                        it.copy(isLoading = false, globalError = result.error.toUiText())
                    }
                }
            }
        }
    }

    private fun register() {
        val errors = validateRegister(_state.value)
        if (errors.isNotEmpty()) {
            _state.update { applyValidationErrors(it.copy(globalError = null), errors) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, globalError = null) }
            val state = _state.value
            when (
                val result = interactors.register(
                    email = state.email,
                    password = state.password,
                    firstName = state.firstName,
                    lastName = state.lastName,
                    telegramNick = state.telegramNick,
                    phone = state.phone,
                )
            ) {
                is AppResult.Success -> {
                    sessionManager.startSession(result.value)
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                    _effects.emit(AuthEffect.Authenticated)
                }
                is AppResult.Failure -> {
                    _state.update {
                        it.copy(isLoading = false, globalError = result.error.toUiText())
                    }
                }
            }
        }
    }
}

sealed interface AuthEffect : UiEffect {
    data object Authenticated : AuthEffect
}
