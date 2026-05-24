package com.predicta.app.feature_auth.presentation

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predicta.app.feature_auth.data.session.UserSessionManager
import com.predicta.app.feature_auth.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: UserSessionManager,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.EmailChanged -> {
                _state.update { it.copy(email = event.value, emailError = null, globalError = null) }
            }
            is AuthEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.value, passwordError = null, globalError = null) }
            }
            is AuthEvent.NameChanged -> {
                _state.update { it.copy(name = event.value, nameError = null, globalError = null) }
            }
            is AuthEvent.RecoveryCodeChanged -> {
                _state.update { it.copy(recoveryCode = event.value, recoveryCodeError = null, globalError = null) }
            }
            is AuthEvent.NewPasswordChanged -> {
                _state.update {
                    it.copy(
                        newPassword = event.value,
                        newPasswordError = null,
                        confirmPasswordError = null,
                        globalError = null
                    )
                }
            }
            is AuthEvent.ConfirmPasswordChanged -> {
                _state.update {
                    it.copy(
                        confirmPassword = event.value,
                        newPasswordError = null,
                        confirmPasswordError = null,
                        globalError = null
                    )
                }
            }
            AuthEvent.LoginSubmit -> login()
            AuthEvent.RegisterSubmit -> register()
            AuthEvent.ResetSubmit -> submitEmailForReset()
            AuthEvent.SubmitEmailForReset -> submitEmailForReset()
            AuthEvent.SubmitRecoveryCode -> submitRecoveryCode()
            AuthEvent.SubmitNewPasswords -> submitNewPasswords()
            AuthEvent.ResetSuccessState -> {
                _state.update { it.copy(isSuccess = false) }
            }
            AuthEvent.ResetPasswordRecoveryStep -> {
                _state.update {
                    it.copy(
                        resetStep = ResetStep.EMAIL_INPUT,
                        recoveryCode = "",
                        recoveryCodeError = null,
                        newPassword = "",
                        newPasswordError = null,
                        confirmPassword = "",
                        confirmPasswordError = null,
                        email = "",
                        emailError = null,
                        globalError = null
                    )
                }
            }
        }
    }

    private fun login() {
        val emailError = validateEmail(_state.value.email)
        val passwordError = validatePassword(_state.value.password)

        if (emailError != null || passwordError != null) {
            _state.update { it.copy(emailError = emailError, passwordError = passwordError) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, globalError = null) }
            val result = authRepository.login(_state.value.email, _state.value.password)
            result.onSuccess { user ->
                sessionManager.startSession(user)
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, globalError = e.message) }
            }
        }
    }

    private fun register() {
        val emailError = validateEmail(_state.value.email)
        val passwordError = validatePassword(_state.value.password)
        val nameError = if (_state.value.name.isBlank()) "Имя не может быть пустым" else null

        if (emailError != null || passwordError != null || nameError != null) {
            _state.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError,
                    nameError = nameError
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, globalError = null) }
            val result = authRepository.register(_state.value.email, _state.value.password, _state.value.name)
            result.onSuccess { user ->
                sessionManager.startSession(user)
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, globalError = e.message) }
            }
        }
    }

    private fun submitEmailForReset() {
        val emailError = validateEmail(_state.value.email)
        if (emailError != null) {
            _state.update { it.copy(emailError = emailError) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, globalError = null) }
            val result = authRepository.resetPassword(_state.value.email)
            result.onSuccess {
                _state.update { it.copy(isLoading = false, resetStep = ResetStep.CODE_VERIFICATION) }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, globalError = e.message) }
            }
        }
    }

    private fun submitRecoveryCode() {
        val code = _state.value.recoveryCode
        if (code.isBlank()) {
            _state.update { it.copy(recoveryCodeError = "Код не может быть пустым") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, globalError = null) }
            delay(500)
            _state.update { it.copy(isLoading = false, resetStep = ResetStep.NEW_PASSWORD) }
        }
    }

    private fun submitNewPasswords() {
        val newPass = _state.value.newPassword
        val confirmPass = _state.value.confirmPassword

        if (newPass.length < 5) {
            _state.update {
                it.copy(
                    newPasswordError = "Минимум 5 символов",
                    confirmPasswordError = "Минимум 5 символов"
                )
            }
            return
        }

        if (newPass != confirmPass) {
            _state.update {
                it.copy(
                    newPasswordError = null,
                    confirmPasswordError = "Пароли не совпадают"
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, globalError = null) }
            delay(500)
            _state.update { it.copy(isLoading = false, resetStep = ResetStep.SUCCESS) }
        }
    }

    private fun validateEmail(email: String): String? {
        if (email.isBlank()) return "Электронная почта не может быть пустой"
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Неверный формат электронной почты"
        return null
    }

    private fun validatePassword(password: String): String? {
        if (password.isBlank()) return "Пароль не может быть пустым"
        if (password.length < 5) return "Минимум 5 символов"
        return null
    }
}
