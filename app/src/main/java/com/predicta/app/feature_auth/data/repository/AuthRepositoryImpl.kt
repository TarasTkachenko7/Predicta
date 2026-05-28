package com.predicta.app.feature_auth.data.repository

import com.predicta.app.core.error.AppError
import com.predicta.app.core.error.AppResult
import com.predicta.app.core.network.ApiCallExecutor
import com.predicta.app.data.remote.PredictaApi
import com.predicta.app.data.remote.dto.LoginRequestDto
import com.predicta.app.data.remote.dto.ManagerDto
import com.predicta.app.data.remote.dto.RegisterRequestDto
import com.predicta.app.feature_auth.data.session.UserSessionManager
import com.predicta.app.feature_auth.domain.model.User
import com.predicta.app.feature_auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: PredictaApi,
    private val apiCallExecutor: ApiCallExecutor,
    private val sessionManager: UserSessionManager,
) : AuthRepository {

    override suspend fun login(email: String, password: String): AppResult<User> {
        return when (
            val loginResult = apiCallExecutor.execute {
                api.login(LoginRequestDto(email = email, password = password))
            }
        ) {
            is AppResult.Success -> {
                val token = loginResult.value.token
                if (token.isBlank()) {
                    return AppResult.Failure(AppError.Auth)
                }

                sessionManager.saveToken(token)
                when (
                    val meResult = apiCallExecutor.execute {
                        api.getCurrentManager()
                    }
                ) {
                    is AppResult.Success -> AppResult.Success(meResult.value.toDomain(fallbackEmail = email))
                    is AppResult.Failure -> meResult
                }
            }
            is AppResult.Failure -> loginResult
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        telegramNick: String,
        phone: String,
    ): AppResult<User> {
        return when (
            val registerResult = apiCallExecutor.execute {
                api.register(
                    RegisterRequestDto(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        password = password,
                        telegramNick = telegramNick,
                        phone = phone,
                    ),
                )
            }
        ) {
            is AppResult.Success -> login(email = email, password = password)
            is AppResult.Failure -> registerResult
        }
    }

    private fun ManagerDto.toDomain(fallbackEmail: String): User {
        val resolvedEmail = email ?: fallbackEmail
        return User(
            id = id ?: managerId ?: accountId ?: resolvedEmail,
            email = resolvedEmail,
            name = listOfNotNull(firstName, lastName)
                .joinToString(" ")
                .takeIf { it.isNotBlank() }
                ?: name
                ?: fullName
                ?: displayName
                ?: jiraDisplayName
                ?: resolvedEmail,
            role = role ?: "manager",
        )
    }
}
