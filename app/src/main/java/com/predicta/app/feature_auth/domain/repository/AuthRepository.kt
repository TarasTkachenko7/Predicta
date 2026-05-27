package com.predicta.app.feature_auth.domain.repository

import com.predicta.app.core.error.AppResult
import com.predicta.app.feature_auth.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): AppResult<User>
    suspend fun register(email: String, password: String, name: String): AppResult<User>
    suspend fun resetPassword(email: String): AppResult<Unit>
}
