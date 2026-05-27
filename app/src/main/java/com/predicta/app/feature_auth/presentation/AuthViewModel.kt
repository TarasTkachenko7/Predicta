package com.predicta.app.feature_auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predicta.app.core.error.AppResult
import com.predicta.app.core.ui.UiEffect
import com.predicta.app.core.ui.toUiText
import com.predicta.app.feature_auth.data.session.UserSessionManager
import com.predicta.app.feature_auth.domain.usecase.AuthInteractors
import kotlinx.coroutines.delay
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
            is AuthEvent.NameChanged,
            AuthEvent.FillDemoCredentials,
            is AuthEvent.RecoveryCodeChanged,
            is AuthEvent.NewPasswordChanged,
            is AuthEvent.ConfirmPasswordChanged,
            AuthEvent.ResetSuccessState,
            AuthEvent.ResetPasswordRecoveryStep,
            -> _state.update { reduceAuthInput(it, event) }

            AuthEvent.LoginDemoSubmit -> {
                _state.update { reduceAuthInput(it, event) }
                login()
            }
            AuthEvent.LoginSubmit -> login()
            AuthEvent.RegisterSubmit -> register()
            AuthEvent.ResetSubmit,
            AuthEvent.SubmitEmailForReset,
            -> submitEmailForReset()
            AuthEvent.SubmitRecoveryCode -> submitRecoveryCode()
            AuthEvent.SubmitNewPasswords -> submitNewPasswords()
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
            when (val result = interactors.register(_state.value.email, _state.value.password, _state.value.name)) {
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

    private fun submitEmailForReset() {
        val errors = validateResetEmail(_state.value)
        if (errors.isNotEmpty()) {
            _state.update { applyValidationErrors(it.copy(globalError = null), errors) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, globalError = null) }
            when (val result = interactors.resetPassword(_state.value.email)) {
                is AppResult.Success -> {
                    _state.update { it.copy(isLoading = false, resetStep = ResetStep.CODE_VERIFICATION) }
                }
                is AppResult.Failure -> {
                    _state.update {
                        it.copy(isLoading = false, globalError = result.error.toUiText())
                    }
                }
            }
        }
    }

    private fun submitRecoveryCode() {
        val errors = validateRecoveryCode(_state.value)
        if (errors.isNotEmpty()) {
            _state.update { applyValidationErrors(it.copy(globalError = null), errors) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, globalError = null) }
            delay(500)
            _state.update { it.copy(isLoading = false, resetStep = ResetStep.NEW_PASSWORD) }
        }
    }

    private fun submitNewPasswords() {
        val errors = validateNewPasswords(_state.value)
        if (errors.isNotEmpty()) {
            _state.update { applyValidationErrors(it.copy(globalError = null), errors) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, globalError = null) }
            delay(500)
            _state.update { it.copy(isLoading = false, resetStep = ResetStep.SUCCESS) }
        }
    }
}

sealed interface AuthEffect : UiEffect {
    data object Authenticated : AuthEffect
}
