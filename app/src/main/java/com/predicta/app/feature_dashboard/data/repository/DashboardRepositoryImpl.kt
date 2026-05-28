package com.predicta.app.feature_dashboard.data.repository

import com.predicta.app.core.error.AppResult
import com.predicta.app.core.network.ApiCallExecutor
import com.predicta.app.data.remote.PredictaApi
import com.predicta.app.data.remote.dto.Health
import com.predicta.app.data.remote.dto.ProjectStatusDto
import com.predicta.app.data.remote.dto.ReassignTaskRequestDto
import com.predicta.app.data.remote.dto.TeamVelocityEmployeeDto
import com.predicta.app.core.ui.formatBackendText
import com.predicta.app.feature_dashboard.domain.model.DashboardSnapshot
import com.predicta.app.feature_dashboard.domain.model.GlobalAlert
import com.predicta.app.feature_dashboard.domain.model.TeamPace
import com.predicta.app.feature_dashboard.domain.repository.DashboardRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class DashboardRepositoryImpl(
    private val api: PredictaApi,
    private val apiCallExecutor: ApiCallExecutor,
) : DashboardRepository {

    override suspend fun getSnapshot(): AppResult<DashboardSnapshot> {
        return coroutineScope {
            val projectStatusRequest = async { apiCallExecutor.execute { api.getProjectStatus() } }
            val teamVelocityRequest = async { apiCallExecutor.execute { api.getTeamVelocity() } }

            val projectStatus = when (val result = projectStatusRequest.await()) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> return@coroutineScope result
            }
            val teamVelocity = when (val result = teamVelocityRequest.await()) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> return@coroutineScope result
            }

            AppResult.Success(projectStatus.toDashboardSnapshot(teamVelocity))
        }
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
            is AppResult.Success -> result.value
                .mapIndexed { index, employee ->
                    TeamPace(
                        day = employee.displayName ?: employee.name ?: "${index + 1}",
                        completedCount = employee.doneCount ?: 0,
                        totalCount = employee.totalCount ?: 0,
                        isRisky = employee.isRisky,
                    )
                }.let { AppResult.Success(it) }
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
        val teamHasRisk = team.any { it.isRisky }

        return DashboardSnapshot(
            sprintNumber = 0,
            sprintName = sprintName ?: sprintStatus ?: status ?: projectStatus ?: "Спринт",
            isProjectDelayed = teamHasRisk,
            delayDays = if (teamHasRisk) delayDays ?: 0 else 0,
            delayTrack = if (teamHasRisk) trackName ?: "" else "",
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
            teamPace = team.mapIndexed { index, employee ->
                TeamPace(
                    day = employee.displayName ?: employee.name ?: "${index + 1}",
                    completedCount = employee.doneCount ?: 0,
                    totalCount = employee.totalCount ?: 0,
                    isRisky = employee.isRisky,
                )
            },
            aiInsight = (aiAdviceText ?: aiInsight.orEmpty()).formatBackendText(),
            predictedDays = 0,
            deadlineDays = if (teamHasRisk) daysRemaining ?: 0 else 0,
            riskFactors = if (teamHasRisk) listOfNotNull(riskMessage) else emptyList(),
            hasBeenReassigned = false,
            isDeepWorkActive = false,
        )
    }

    private fun ProjectStatusDto.toAlerts(): List<GlobalAlert> {
        return listOfNotNull(
            riskMessage?.let {
                GlobalAlert(
                    id = "project_risk",
                    message = it.formatBackendText(),
                    severity = if (isAtRisk == true) "high" else "medium",
                )
            },
            aiAdviceText?.let {
                GlobalAlert(
                    id = "ai_advice",
                    message = it.formatBackendText(),
                    severity = "medium",
                )
            },
        )
    }

    private val TeamVelocityEmployeeDto.idValue: String
        get() = accountId ?: id.orEmpty()

    private val TeamVelocityEmployeeDto.isRisky: Boolean
        get() = when (health) {
            Health.bad -> true
            Health.normal, Health.good, null -> (burnoutRisk ?: 0f) >= 0.7f
        }

    private val ProjectStatusDto.aiAdviceText: String?
        get() = aiAdvice?.toString()?.trim('"')
}
