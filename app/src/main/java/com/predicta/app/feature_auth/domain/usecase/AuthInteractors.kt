package com.predicta.app.feature_auth.domain.usecase

data class AuthInteractors(
    val login: LoginUseCase,
    val register: RegisterUseCase,
)
