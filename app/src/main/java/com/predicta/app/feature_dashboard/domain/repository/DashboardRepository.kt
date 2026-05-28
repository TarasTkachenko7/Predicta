package com.predicta.app.feature_dashboard.domain.repository

import com.predicta.app.core.error.AppResult
import com.predicta.app.feature_dashboard.domain.model.GlobalAlert
import com.predicta.app.feature_dashboard.domain.model.DashboardSnapshot
import com.predicta.app.feature_dashboard.domain.model.TeamPace

interface DashboardRepository {
    suspend fun getSnapshot(): AppResult<DashboardSnapshot>
    suspend fun reassignTask(taskId: String, newExecutorId: String): AppResult<Unit>
    suspend fun getTeamPace(): AppResult<List<TeamPace>>
    suspend fun getGlobalAlerts(): AppResult<List<GlobalAlert>>
}
