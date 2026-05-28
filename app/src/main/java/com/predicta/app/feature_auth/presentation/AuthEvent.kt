package com.predicta.app.feature_auth.presentation

sealed interface AuthEvent {
    data class EmailChanged(val value: String) : AuthEvent
    data class PasswordChanged(val value: String) : AuthEvent
    data class FirstNameChanged(val value: String) : AuthEvent
    data class LastNameChanged(val value: String) : AuthEvent
    data class TelegramNickChanged(val value: String) : AuthEvent
    data class PhoneChanged(val value: String) : AuthEvent
    data object LoginSubmit : AuthEvent
    data object RegisterSubmit : AuthEvent
    data object ResetSuccessState : AuthEvent
}
