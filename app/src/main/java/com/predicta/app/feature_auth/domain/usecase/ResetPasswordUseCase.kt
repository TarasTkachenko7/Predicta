package com.predicta.app.feature_auth.domain.usecase

import com.predicta.app.core.error.AppResult
import com.predicta.app.feature_auth.domain.repository.AuthRepository

class ResetPasswordUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): AppResult<Unit> {
        return repository.resetPassword(email)
    }
}
