package com.predicta.app.feature_employees.presentation

import com.predicta.app.data.demo.DemoData

/**
 * Immutable UI state for the Team Velocity screen.
 */
data class EmployeeState(
    val isLoading: Boolean = true,
    val demoData: DemoData? = null,
    val selectedEmployeeId: String? = null,
    val error: String? = null,
)
