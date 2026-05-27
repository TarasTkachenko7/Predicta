package com.predicta.app.feature_auth.data.repository

import com.predicta.app.core.error.AppError
import com.predicta.app.core.error.AppResult
import com.predicta.app.core.error.ValidationField
import com.predicta.app.core.error.ValidationReason
import com.predicta.app.feature_auth.domain.model.User
import com.predicta.app.feature_auth.domain.repository.AuthRepository
import kotlinx.coroutines.delay

class AuthRepositoryImpl : AuthRepository {

    override suspend fun login(email: String, password: String): AppResult<User> {
        delay(500) // Simulate network delay
        if (email.isNotBlank() && password.isNotBlank()) {
            return AppResult.Success(
                User(
                    id = "user_123",
                    email = email,
                    name = "Demo Manager",
                    role = "manager",
                )
            )
        }
        return AppResult.Failure(AppError.Auth)
    }

    override suspend fun register(email: String, password: String, name: String): AppResult<User> {
        delay(500) // Simulate network delay
        if (email.isNotBlank() && password.isNotBlank() && name.isNotBlank()) {
            return AppResult.Success(
                User(
                    id = "user_new",
                    email = email,
                    name = name,
                    role = "manager",
                )
            )
        }
        return AppResult.Failure(AppError.Validation(ValidationField.EMAIL, ValidationReason.BLANK))
    }

    override suspend fun resetPassword(email: String): AppResult<Unit> {
        delay(500) // Simulate network delay
        if (email.isNotBlank()) {
            return AppResult.Success(Unit)
        }
        return AppResult.Failure(AppError.Validation(ValidationField.EMAIL, ValidationReason.BLANK))
    }
}
