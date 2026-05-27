package com.predicta.app.feature_dashboard.domain.model

/**
 * Domain snapshot of the demo dashboard scenario.
 */
data class DashboardSnapshot(
    val sprintNumber: Int,
    val sprintName: String,
    val isProjectDelayed: Boolean,
    val delayDays: Int,
    val delayTrack: String,
    val sprintCompletionPercent: Float,
    val sprintTotalDays: Int,
    val sprintElapsedDays: Int,
    val olegId: String,
    val olegName: String,
    val olegRole: String,
    val olegDone: Int,
    val olegTotal: Int,
    val pavelId: String,
    val pavelName: String,
    val pavelRole: String,
    val pavelDone: Int,
    val pavelTotal: Int,
    val pavelTasks: List<DashboardTask>,
    val pavelAiInsight: String,
    val pavelPredictedDays: Int,
    val pavelDeadlineDays: Int,
    val pavelRiskFactors: List<String>,
    val hasBeenReassigned: Boolean,
    val isDeepWorkActive: Boolean,
)

data class DashboardTask(
    val id: String,
    val title: String,
    val status: DashboardTaskStatus,
    val assigneeId: String,
    val assigneeName: String,
)

enum class DashboardTaskStatus {
    TODO,
    IN_PROGRESS,
    DONE,
    REASSIGNED,
}

object DashboardEmployeeIds {
    const val OLEG_ID = "emp_oleg"
    const val PAVEL_ID = "emp_pavel"
}
