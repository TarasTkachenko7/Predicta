package com.predicta.app.feature_employees.presentation

/**
 * One-shot events the Team Velocity UI can send to the ViewModel.
 */
sealed interface EmployeeEvent {
    data object Refresh : EmployeeEvent
    data class SelectEmployee(val employeeId: String) : EmployeeEvent
}
