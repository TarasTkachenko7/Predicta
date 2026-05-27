package com.predicta.app.feature_auth.presentation

import com.predicta.app.core.error.AppError
import com.predicta.app.core.error.ValidationField
import com.predicta.app.core.error.ValidationReason
import com.predicta.app.core.ui.toUiText

fun reduceAuthInput(state: AuthState, event: AuthEvent): AuthState {
    return when (event) {
        is AuthEvent.EmailChanged -> state.copy(email = event.value, emailError = null, globalError = null)
        is AuthEvent.PasswordChanged -> state.copy(password = event.value, passwordError = null, globalError = null)
        is AuthEvent.NameChanged -> state.copy(name = event.value, nameError = null, globalError = null)
        is AuthEvent.RecoveryCodeChanged -> state.copy(
            recoveryCode = event.value,
            recoveryCodeError = null,
            globalError = null,
        )
        is AuthEvent.NewPasswordChanged -> state.copy(
            newPassword = event.value,
            newPasswordError = null,
            confirmPasswordError = null,
            globalError = null,
        )
        is AuthEvent.ConfirmPasswordChanged -> state.copy(
            confirmPassword = event.value,
            newPasswordError = null,
            confirmPasswordError = null,
            globalError = null,
        )
        AuthEvent.FillDemoCredentials,
        AuthEvent.LoginDemoSubmit,
        -> state.copy(
            email = "demo@predicta.ai",
            password = "demo123",
            emailError = null,
            passwordError = null,
            globalError = null,
        )
        AuthEvent.ResetSuccessState -> state.copy(isSuccess = false)
        AuthEvent.ResetPasswordRecoveryStep -> initialRecoveryState()
        else -> state
    }
}

fun validateLogin(state: AuthState): List<AppError.Validation> {
    return listOfNotNull(
        validateEmail(state.email),
        validatePassword(state.password),
    )
}

fun validateRegister(state: AuthState): List<AppError.Validation> {
    return listOfNotNull(
        validateEmail(state.email),
        validatePassword(state.password),
        if (state.name.isBlank()) {
            AppError.Validation(ValidationField.NAME, ValidationReason.BLANK)
        } else {
            null
        },
    )
}

fun validateResetEmail(state: AuthState): List<AppError.Validation> {
    return listOfNotNull(validateEmail(state.email))
}

fun validateRecoveryCode(state: AuthState): List<AppError.Validation> {
    return if (state.recoveryCode.isBlank()) {
        listOf(AppError.Validation(ValidationField.RECOVERY_CODE, ValidationReason.BLANK))
    } else {
        emptyList()
    }
}

fun validateNewPasswords(state: AuthState): List<AppError.Validation> {
    return when {
        state.newPassword.length < MIN_PASSWORD_LENGTH -> listOf(
            AppError.Validation(ValidationField.PASSWORD, ValidationReason.TOO_SHORT),
            AppError.Validation(ValidationField.CONFIRM_PASSWORD, ValidationReason.TOO_SHORT),
        )
        state.newPassword != state.confirmPassword -> listOf(
            AppError.Validation(ValidationField.CONFIRM_PASSWORD, ValidationReason.MISMATCH),
        )
        else -> emptyList()
    }
}

fun applyValidationErrors(state: AuthState, errors: List<AppError.Validation>): AuthState {
    return errors.fold(state) { current, error ->
        val message = error.toUiText()
        when (error.field) {
            ValidationField.EMAIL -> current.copy(emailError = message)
            ValidationField.PASSWORD -> current.copy(passwordError = message, newPasswordError = message)
            ValidationField.NAME -> current.copy(nameError = message)
            ValidationField.RECOVERY_CODE -> current.copy(recoveryCodeError = message)
            ValidationField.CONFIRM_PASSWORD -> current.copy(confirmPasswordError = message)
        }
    }
}

private fun validateEmail(email: String): AppError.Validation? {
    if (email.isBlank()) {
        return AppError.Validation(ValidationField.EMAIL, ValidationReason.BLANK)
    }
    if (!EMAIL_REGEX.matches(email)) {
        return AppError.Validation(ValidationField.EMAIL, ValidationReason.INVALID_EMAIL)
    }
    return null
}

private fun validatePassword(password: String): AppError.Validation? {
    if (password.isBlank()) {
        return AppError.Validation(ValidationField.PASSWORD, ValidationReason.BLANK)
    }
    if (password.length < MIN_PASSWORD_LENGTH) {
        return AppError.Validation(ValidationField.PASSWORD, ValidationReason.TOO_SHORT)
    }
    return null
}

private fun initialRecoveryState(): AuthState {
    return AuthState(
        resetStep = ResetStep.EMAIL_INPUT,
    )
}

private const val MIN_PASSWORD_LENGTH = 5
private val EMAIL_REGEX = Regex("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", RegexOption.IGNORE_CASE)
