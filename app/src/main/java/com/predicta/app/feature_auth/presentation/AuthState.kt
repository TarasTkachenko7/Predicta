package com.predicta.app.feature_auth.presentation

data class AuthState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val firstName: String = "",
    val firstNameError: String? = null,
    val lastName: String = "",
    val lastNameError: String? = null,
    val telegramNick: String = "",
    val telegramNickError: String? = null,
    val phone: String = "",
    val phoneError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val globalError: String? = null,
)
