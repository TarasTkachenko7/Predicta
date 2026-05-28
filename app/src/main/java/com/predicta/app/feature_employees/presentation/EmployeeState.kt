package com.predicta.app.feature_employees.presentation

import com.predicta.app.feature_employees.domain.model.Employee

data class EmployeeState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val employees: List<Employee> = emptyList(),
    val selectedEmployeeId: String? = null,
    val error: String? = null,
)
