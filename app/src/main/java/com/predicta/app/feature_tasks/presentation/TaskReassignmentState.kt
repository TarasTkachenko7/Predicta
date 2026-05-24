package com.predicta.app.feature_tasks.presentation

data class TaskReassignmentState(
    val isLoading: Boolean = true,
    val taskId: String = "",
    val taskTitle: String = "",
    val fromName: String = "",
    val toName: String = "",
    val toRole: String = "",
    val toDone: Int = 0,
    val toTotal: Int = 0,
    val canReassign: Boolean = false,
    val isReassigned: Boolean = false,
)
