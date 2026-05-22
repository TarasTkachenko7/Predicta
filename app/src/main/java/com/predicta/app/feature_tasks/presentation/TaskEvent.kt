package com.predicta.app.feature_tasks.presentation

import com.predicta.app.feature_employees.domain.model.Employee

/**
 * Events the Task Assignment UI can send to the ViewModel.
 */
sealed interface TaskEvent {
    data class UpdateDescription(val text: String) : TaskEvent
    data class SelectEmployee(val employee: Employee) : TaskEvent
    data object ToggleDropdown : TaskEvent
    data object DismissDropdown : TaskEvent
    data object DismissRecommendation : TaskEvent
}
