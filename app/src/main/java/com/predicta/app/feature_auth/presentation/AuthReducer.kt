package com.predicta.app.feature_auth.presentation

import com.predicta.app.core.error.AppError
import com.predicta.app.core.error.ValidationField
import com.predicta.app.core.error.ValidationReason
import com.predicta.app.core.ui.toUiText

fun reduceAuthInput(state: AuthState, event: AuthEvent): AuthState {
    return when (event) {
        is AuthEvent.EmailChanged -> state.copy(email = event.value, emailError = null, globalError = null)
        is AuthEvent.PasswordChanged -> state.copy(password = event.value, passwordError = null, globalError = null)
        is AuthEvent.FirstNameChanged -> state.copy(firstName = event.value, firstNameError = null, globalError = null)
        is AuthEvent.LastNameChanged -> state.copy(lastName = event.value, lastNameError = null, globalError = null)
        is AuthEvent.TelegramNickChanged -> state.copy(
            telegramNick = event.value,
            telegramNickError = null,
            globalError = null,
        )
        is AuthEvent.PhoneChanged -> state.copy(phone = event.value, phoneError = null, globalError = null)
        AuthEvent.ResetSuccessState -> state.copy(isSuccess = false)
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
        if (state.firstName.isBlank()) {
            AppError.Validation(ValidationField.FIRST_NAME, ValidationReason.BLANK)
        } else {
            null
        },
        if (state.lastName.isBlank()) {
            AppError.Validation(ValidationField.LAST_NAME, ValidationReason.BLANK)
        } else {
            null
        },
        if (state.telegramNick.isBlank()) {
            AppError.Validation(ValidationField.TELEGRAM_NICK, ValidationReason.BLANK)
        } else {
            null
        },
        if (state.phone.isBlank()) {
            AppError.Validation(ValidationField.PHONE, ValidationReason.BLANK)
        } else {
            null
        },
    )
}

fun applyValidationErrors(state: AuthState, errors: List<AppError.Validation>): AuthState {
    return errors.fold(state) { current, error ->
        val message = error.toUiText()
        when (error.field) {
            ValidationField.EMAIL -> current.copy(emailError = message)
            ValidationField.PASSWORD -> current.copy(passwordError = message)
            ValidationField.NAME,
            ValidationField.FIRST_NAME,
            -> current.copy(firstNameError = message)
            ValidationField.LAST_NAME -> current.copy(lastNameError = message)
            ValidationField.TELEGRAM_NICK -> current.copy(telegramNickError = message)
            ValidationField.PHONE -> current.copy(phoneError = message)
            ValidationField.RECOVERY_CODE,
            ValidationField.CONFIRM_PASSWORD,
            -> current
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

private const val MIN_PASSWORD_LENGTH = 6
private val EMAIL_REGEX = Regex("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", RegexOption.IGNORE_CASE)
