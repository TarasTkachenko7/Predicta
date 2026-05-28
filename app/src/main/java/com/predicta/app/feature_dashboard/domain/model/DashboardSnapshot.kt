package com.predicta.app.feature_dashboard.domain.model

data class DashboardSnapshot(
    val sprintNumber: Int,
    val sprintName: String,
    val isProjectDelayed: Boolean,
    val delayDays: Int,
    val delayTrack: String,
    val sprintCompletionPercent: Float,
    val sprintTotalDays: Int,
    val sprintElapsedDays: Int,
    val primaryEmployeeId: String,
    val primaryEmployeeName: String,
    val primaryEmployeeRole: String,
    val primaryEmployeeDone: Int,
    val primaryEmployeeTotal: Int,
    val secondaryEmployeeId: String,
    val secondaryEmployeeName: String,
    val secondaryEmployeeRole: String,
    val secondaryEmployeeDone: Int,
    val secondaryEmployeeTotal: Int,
    val secondaryEmployeeTasks: List<DashboardTask>,
    val teamPace: List<TeamPace>,
    val aiInsight: String,
    val predictedDays: Int,
    val deadlineDays: Int,
    val riskFactors: List<String>,
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
