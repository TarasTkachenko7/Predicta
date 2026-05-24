package com.predicta.app.feature_auth.data.repository

import com.predicta.app.feature_auth.domain.model.User
import com.predicta.app.feature_auth.domain.repository.AuthRepository
import kotlinx.coroutines.delay

class AuthRepositoryImpl : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        delay(500) // Simulate network delay
        if (email.isNotBlank() && password.isNotBlank()) {
            return Result.success(
                User(
                    id = "user_123",
                    email = email,
                    name = "Demo Manager",
                    role = "manager",
                )
            )
        }
        return Result.failure(Exception("Неверные учетные данные"))
    }

    override suspend fun register(email: String, password: String, name: String): Result<User> {
        delay(500) // Simulate network delay
        if (email.isNotBlank() && password.isNotBlank() && name.isNotBlank()) {
            return Result.success(
                User(
                    id = "user_new",
                    email = email,
                    name = name,
                    role = "manager",
                )
            )
        }
        return Result.failure(Exception("Неверные регистрационные данные"))
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        delay(500) // Simulate network delay
        if (email.isNotBlank()) {
            return Result.success(Unit)
        }
        return Result.failure(Exception("Необходимо указать адрес электронной почты"))
    }
}
