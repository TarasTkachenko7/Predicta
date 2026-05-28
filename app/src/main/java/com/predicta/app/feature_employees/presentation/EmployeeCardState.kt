package com.predicta.app.feature_employees.presentation

import com.predicta.app.feature_dashboard.domain.model.DashboardTask

data class EmployeeCardState(
    val isLoading: Boolean = true,
    val employeeId: String = "",
    val showAnalytics: Boolean = true,
    val name: String = "",
    val role: String = "",
    val avatarUrl: String? = null,
    val done: Int = 0,
    val total: Int = 0,
    val isHealthy: Boolean = false,
    val predictedDays: Int = 0,
    val deadlineDays: Int = 0,
    val aiInsight: String = "",
    val riskFactors: List<String> = emptyList(),
    val tasks: List<DashboardTask> = emptyList(),
    val isDeepWorkActive: Boolean = false,
)
