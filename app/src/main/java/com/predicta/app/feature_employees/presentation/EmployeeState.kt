package com.predicta.app.feature_employees.presentation

import com.predicta.app.feature_dashboard.domain.model.DashboardSnapshot

/**
 * Immutable UI state for the Team Velocity screen.
 */
data class EmployeeState(
    val isLoading: Boolean = true,
    val demoData: DashboardSnapshot? = null,
    val selectedEmployeeId: String? = null,
    val error: String? = null,
)
