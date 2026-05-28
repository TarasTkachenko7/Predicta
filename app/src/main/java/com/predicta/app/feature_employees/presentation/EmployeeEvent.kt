package com.predicta.app.feature_employees.presentation


sealed interface EmployeeEvent {
    data object Refresh : EmployeeEvent
    data class SelectEmployee(val employeeId: String) : EmployeeEvent
}

