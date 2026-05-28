package com.predicta.app.feature_auth.domain.repository

import com.predicta.app.core.error.AppResult
import com.predicta.app.feature_auth.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): AppResult<User>
    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        telegramNick: String,
        phone: String,
    ): AppResult<User>
}
