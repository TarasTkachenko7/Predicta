package com.predicta.app.feature_employees.domain.model


data class Employee(
    val id: String,
    val name: String,
    val role: String,
    val avatarUrl: String? = null,
    val workloadPercentage: Float,
    val burnoutRisk: Float,
    val doneCount: Int = 0,
    val totalCount: Int = 0,
)

