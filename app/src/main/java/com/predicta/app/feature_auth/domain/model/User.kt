package com.predicta.app.feature_auth.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: String,
)
