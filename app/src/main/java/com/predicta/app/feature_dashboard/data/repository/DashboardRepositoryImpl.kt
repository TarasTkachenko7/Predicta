package com.predicta.app.feature_dashboard.data.repository

import com.predicta.app.core.error.AppResult
import com.predicta.app.core.network.ApiCallExecutor
import com.predicta.app.data.remote.PredictaApi
import com.predicta.app.data.remote.dto.ProjectStatusDto
import com.predicta.app.data.remote.dto.ReassignTaskRequestDto
import com.predicta.app.data.remote.dto.TeamVelocityEmployeeDto
import com.predicta.app.feature_dashboard.domain.model.DashboardSnapshot
import com.predicta.app.feature_dashboard.domain.model.GlobalAlert
import com.predicta.app.feature_dashboard.domain.model.TeamPace
import com.predicta.app.feature_dashboard.domain.repository.DashboardRepository

class DashboardRepositoryImpl(
    private val api: PredictaApi,
    private val apiCallExecutor: ApiCallExecutor,
) : DashboardRepository {

    override suspend fun getSnapshot(): AppResult<DashboardSnapshot> {
        val projectStatus = when (val result = apiCallExecutor.execute { api.getProjectStatus() }) {
            is AppResult.Success -> result.value
            is AppResult.Failure -> return result
        }
        val teamVelocity = when (val result = apiCallExecutor.execute { api.getTeamVelocity() }) {
            is AppResult.Success -> result.value
            is AppResult.Failure -> return result
        }

        return AppResult.Success(projectStatus.toDashboardSnapshot(teamVelocity))
    }

    override suspend fun reassignTask(taskId: String, newExecutorId: String): AppResult<Unit> {
        return when (
            val result = apiCallExecutor.execute {
                api.reassignTask(
                    ReassignTaskRequestDto(
                        taskId = taskId,
                        newExecutorId = newExecutorId,
                    ),
                )
            }
        ) {
            is AppResult.Success -> AppResult.Success(Unit)
            is AppResult.Failure -> result
        }
    }

    override suspend fun getTeamPace(): AppResult<List<TeamPace>> {
        return when (val result = apiCallExecutor.execute { api.getTeamVelocity() }) {
            is AppResult.Success -> AppResult.Success(
                result.value.mapIndexed { index, employee ->
                    TeamPace(
                        day = employee.displayName ?: employee.name ?: "${index + 1}",
                        velocity = (employee.doneCount ?: 0).toFloat(),
                    )
                },
            )
            is AppResult.Failure -> result
        }
    }

    override suspend fun getGlobalAlerts(): AppResult<List<GlobalAlert>> {
        return when (val result = apiCallExecutor.execute { api.getProjectStatus() }) {
            is AppResult.Success -> AppResult.Success(result.value.toAlerts())
            is AppResult.Failure -> result
        }
    }

    private fun ProjectStatusDto.toDashboardSnapshot(team: List<TeamVelocityEmployeeDto>): DashboardSnapshot {
        val first = team.getOrNull(0)
        val second = team.getOrNull(1)
        val completion = completionPct ?: completionPercent ?: completion ?: 0f
        val normalizedCompletion = if (completion > 1f) completion / 100f else completion
        val risk = isAtRisk ?: ((delayDays ?: 0) > 0)

        return DashboardSnapshot(
            sprintNumber = 0,
            sprintName = sprintName ?: sprintStatus ?: status ?: projectStatus ?: "Спринт",
            isProjectDelayed = risk,
            delayDays = delayDays ?: 0,
            delayTrack = trackName ?: "",
            sprintCompletionPercent = normalizedCompletion.coerceIn(0f, 1f),
            sprintTotalDays = daysRemaining ?: 0,
            sprintElapsedDays = 0,
            primaryEmployeeId = first?.idValue.orEmpty(),
            primaryEmployeeName = first?.displayName ?: first?.name ?: "",
            primaryEmployeeRole = first?.role ?: first?.position ?: "",
            primaryEmployeeDone = first?.doneCount ?: 0,
            primaryEmployeeTotal = first?.totalCount ?: 0,
            secondaryEmployeeId = second?.idValue.orEmpty(),
            secondaryEmployeeName = second?.displayName ?: second?.name ?: "",
            secondaryEmployeeRole = second?.role ?: second?.position ?: "",
            secondaryEmployeeDone = second?.doneCount ?: 0,
            secondaryEmployeeTotal = second?.totalCount ?: 0,
            secondaryEmployeeTasks = emptyList(),
            aiInsight = aiAdviceText ?: aiInsight.orEmpty(),
            predictedDays = 0,
            deadlineDays = daysRemaining ?: 0,
            riskFactors = listOfNotNull(riskMessage),
            hasBeenReassigned = false,
            isDeepWorkActive = false,
        )
    }

    private fun ProjectStatusDto.toAlerts(): List<GlobalAlert> {
        return listOfNotNull(
            riskMessage?.let {
                GlobalAlert(
                    id = "project_risk",
                    message = it,
                    severity = if (isAtRisk == true) "high" else "medium",
                )
            },
            aiAdviceText?.let {
                GlobalAlert(
                    id = "ai_advice",
                    message = it,
                    severity = "medium",
                )
            },
        )
    }

    private val TeamVelocityEmployeeDto.idValue: String
        get() = accountId ?: id.orEmpty()

    private val ProjectStatusDto.aiAdviceText: String?
        get() = aiAdvice?.toString()?.trim('"')
}
