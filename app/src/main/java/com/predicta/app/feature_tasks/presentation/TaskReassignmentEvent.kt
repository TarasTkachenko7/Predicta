package com.predicta.app.feature_tasks.presentation

sealed interface TaskReassignmentEvent {
    data object ConfirmReassignment : TaskReassignmentEvent
    data object CompleteReassignment : TaskReassignmentEvent
}

sealed interface TaskReassignmentNavAction {
    data object GoToDashboard : TaskReassignmentNavAction
}
