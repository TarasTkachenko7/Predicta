package com.predicta.app.feature_tasks.presentation

import com.predicta.app.feature_employees.domain.model.Employee


data class TaskState(
    val isLoading: Boolean = true,
    val taskDescription: String = "",
    val employees: List<Employee> = emptyList(),
    val selectedEmployee: Employee? = null,
    val isDropdownExpanded: Boolean = false,
    val aiRecommendation: String? = null,
    val error: String? = null,
)

