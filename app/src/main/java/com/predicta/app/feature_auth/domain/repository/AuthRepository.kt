package com.predicta.app.feature_auth.domain.repository

import com.predicta.app.feature_auth.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String, name: String): Result<User>
    suspend fun resetPassword(email: String): Result<Unit>
}
