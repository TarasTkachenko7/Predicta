package com.predicta.app.feature_employees.presentation

import com.predicta.app.feature_employees.domain.model.Employee

/**
 * Immutable UI state for the Employee Details screen.
 */
data class EmployeeState(
    val isLoading: Boolean = true,
    val employees: List<Employee> = emptyList(),
    val error: String? = null,
)
