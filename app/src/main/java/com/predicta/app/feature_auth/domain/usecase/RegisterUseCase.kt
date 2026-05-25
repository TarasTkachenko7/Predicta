package com.predicta.app.feature_auth.domain.usecase

import com.predicta.app.feature_auth.domain.model.User
import com.predicta.app.feature_auth.domain.repository.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, name: String): Result<User> {
        return repository.register(email, password, name)
    }
}
