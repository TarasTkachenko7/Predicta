package com.predicta.app.feature_employees.presentation

import com.predicta.app.data.demo.DemoTask

data class EmployeeCardState(
    val isLoading: Boolean = true,
    val employeeId: String = "",
    val isPavel: Boolean = false,
    val name: String = "",
    val role: String = "",
    val done: Int = 0,
    val total: Int = 0,
    val isHealthy: Boolean = false,
    val predictedDays: Int = 0,
    val deadlineDays: Int = 0,
    val aiInsight: String = "",
    val tasks: List<DemoTask> = emptyList(),
)
