package com.predicta.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
enum class Health {
    @SerialName("good")
    good,

    @SerialName("normal")
    normal,

    @SerialName("bad")
    bad,
}

@Serializable
enum class TaskStatus {
    @SerialName("todo")
    todo,

    @SerialName("in_progress")
    in_progress,

    @SerialName("done")
    done,
}

@Serializable
data class ErrorResponseDto(
    val error: String,
)

@Serializable
data class HealthResponseDto(
    val status: String,
)

@Serializable
data class RegisterRequestDto(
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    val email: String,
    val password: String,
    @SerialName("telegram_nick")
    val telegramNick: String,
    val phone: String,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
)

@Serializable
data class MessageResponseDto(
    val message: String,
)

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String,
)

@Serializable
data class LoginResponseDto(
    val token: String,
)

@Serializable
data class ManagerDto(
    val id: String? = null,
    @SerialName("manager_id")
    val managerId: String? = null,
    @SerialName("account_id")
    val accountId: String? = null,
    val email: String? = null,
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("last_name")
    val lastName: String? = null,
    val name: String? = null,
    @SerialName("full_name")
    val fullName: String? = null,
    @SerialName("display_name")
    val displayName: String? = null,
    val role: String? = null,
    @SerialName("telegram_nick")
    val telegramNick: String? = null,
    val phone: String? = null,
    @SerialName("subordinates_count")
    val subordinatesCount: Int? = null,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    @SerialName("jira_display_name")
    val jiraDisplayName: String? = null,
    @SerialName("jira_email")
    val jiraEmail: String? = null,
)

@Serializable
data class ProjectStatusDto(
    val status: String? = null,
    @SerialName("project_status")
    val projectStatus: String? = null,
    @SerialName("sprint_status")
    val sprintStatus: String? = null,
    @SerialName("sprint_name")
    val sprintName: String? = null,
    @SerialName("completion_percent")
    val completionPercent: Float? = null,
    @SerialName("completion_pct")
    val completionPct: Float? = null,
    @SerialName("completion")
    val completion: Float? = null,
    @SerialName("delay_days")
    val delayDays: Int? = null,
    @SerialName("is_at_risk")
    val isAtRisk: Boolean? = null,
    @SerialName("risk_message")
    val riskMessage: String? = null,
    @SerialName("track_name")
    val trackName: String? = null,
    @SerialName("days_remaining")
    val daysRemaining: Int? = null,
    @SerialName("ai_advice")
    val aiAdvice: JsonElement? = null,
    @SerialName("ai_tips")
    val aiTips: JsonElement? = null,
    @SerialName("ai_insight")
    val aiInsight: String? = null,
)

@Serializable
data class TeamVelocityEmployeeDto(
    val id: String? = null,
    @SerialName("account_id")
    val accountId: String? = null,
    val name: String? = null,
    @SerialName("display_name")
    val displayName: String? = null,
    val role: String? = null,
    val position: String? = null,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    val health: Health? = null,
    @SerialName("done_count")
    val doneCount: Int? = null,
    @SerialName("total_count")
    val totalCount: Int? = null,
    val workload: Float? = null,
    @SerialName("workload_percent")
    val workloadPercent: Float? = null,
    @SerialName("workload_percentage")
    val workloadPercentage: Float? = null,
    @SerialName("burnout_risk")
    val burnoutRisk: Float? = null,
)

@Serializable
data class TeamInsightsDto(
    @SerialName("ai_insight")
    val aiInsight: String,
)

@Serializable
data class EmployeeDetailsDto(
    val id: String? = null,
    @SerialName("account_id")
    val accountId: String? = null,
    val name: String? = null,
    @SerialName("display_name")
    val displayName: String? = null,
    val email: String? = null,
    val role: String? = null,
    val position: String? = null,
    @SerialName("telegram_nick")
    val telegramNick: String? = null,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    @SerialName("done_count")
    val doneCount: Int? = null,
    @SerialName("total_count")
    val totalCount: Int? = null,
    @SerialName("remaining_count")
    val remainingCount: Int? = null,
    val health: Health? = null,
    val workload: Float? = null,
    @SerialName("workload_percent")
    val workloadPercent: Float? = null,
    @SerialName("ai_insight")
    val aiInsight: String? = null,
    val tasks: List<TaskDto> = emptyList(),
)

@Serializable
data class TaskDto(
    val id: String? = null,
    val key: String? = null,
    val title: String? = null,
    val summary: String? = null,
    val description: String? = null,
    val status: TaskStatus? = null,
    @SerialName("assignee_id")
    val assigneeId: String? = null,
    @SerialName("assignee_name")
    val assigneeName: String? = null,
    val priority: String? = null,
    @SerialName("estimate_hours")
    val estimateHours: Float? = null,
    @SerialName("due_date")
    val dueDate: String? = null,
)

@Serializable
data class EmployeeAnalyticsDto(
    @SerialName("employee_id")
    val employeeId: String? = null,
    @SerialName("employee_name")
    val employeeName: String? = null,
    @SerialName("account_id")
    val accountId: String? = null,
    val role: String? = null,
    val health: Health? = null,
    val workload: Float? = null,
    @SerialName("workload_percent")
    val workloadPercent: Float? = null,
    @SerialName("burnout_risk")
    val burnoutRisk: Float? = null,
    @SerialName("completed_tasks")
    val completedTasks: Int? = null,
    @SerialName("total_tasks")
    val totalTasks: Int? = null,
    @SerialName("completion_percent")
    val completionPercent: Float? = null,
    @SerialName("predicted_days")
    val predictedDays: Int? = null,
    @SerialName("forecast_days_to_complete")
    val forecastDaysToComplete: Int? = null,
    @SerialName("deadline_days")
    val deadlineDays: Int? = null,
    @SerialName("sprint_days_left")
    val sprintDaysLeft: Int? = null,
    @SerialName("delay_days")
    val delayDays: Int? = null,
    @SerialName("risk_factors")
    val riskFactors: List<String> = emptyList(),
    @SerialName("ai_insight")
    val aiInsight: String? = null,
    val tasks: List<TaskDto> = emptyList(),
)

@Serializable
data class CreateTaskRequestDto(
    val title: String,
    val description: String? = null,
    @SerialName("assignee_id")
    val assigneeId: String? = null,
    val force: Boolean? = null,
)

@Serializable
data class CreateTaskResponseDto(
    val created: Boolean,
    val approved: Boolean? = null,
    val message: String? = null,
    @SerialName("task_id")
    val taskId: String? = null,
    @SerialName("task_title")
    val taskTitle: String? = null,
    val task: TaskDto? = null,
    @SerialName("assignee_id")
    val assigneeId: String? = null,
    @SerialName("assignee_name")
    val assigneeName: String? = null,
    @SerialName("suggested_assignee")
    val suggestedAssignee: SuggestedAssigneeDto? = null,
    @SerialName("suggested_assignee_id")
    val suggestedAssigneeId: String? = null,
    @SerialName("suggested_assignee_name")
    val suggestedAssigneeName: String? = null,
    @SerialName("ai_insight")
    val aiInsight: String? = null,
)

@Serializable
data class SuggestedAssigneeDto(
    val id: String? = null,
    @SerialName("account_id")
    val accountId: String? = null,
    val name: String? = null,
    @SerialName("display_name")
    val displayName: String? = null,
    val health: Health? = null,
    val workload: Float? = null,
    @SerialName("workload_percent")
    val workloadPercent: Float? = null,
)

@Serializable
data class ReassignTaskRequestDto(
    @SerialName("task_id")
    val taskId: String,
    @SerialName("new_executor_id")
    val newExecutorId: String,
)

@Serializable
data class ReassignTaskResponseDto(
    val message: String,
    @SerialName("project_status")
    val projectStatus: ProjectStatusDto? = null,
)
