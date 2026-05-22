package com.predicta.app.feature_employees.presentation

/**
 * One-shot events the UI can send to the ViewModel.
 */
sealed interface EmployeeEvent {
    data object Refresh : EmployeeEvent
}
