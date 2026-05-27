package com.predicta.app.feature_dashboard.domain.repository

import com.predicta.app.feature_dashboard.domain.model.GlobalAlert
import com.predicta.app.feature_dashboard.domain.model.DashboardSnapshot
import com.predicta.app.feature_dashboard.domain.model.TeamPace
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for dashboard data.
 * Implemented by [com.predicta.app.feature_dashboard.data.repository.DashboardRepositoryImpl].
 */
interface DashboardRepository {
    fun observeSnapshot(): Flow<DashboardSnapshot>
    fun reassignTask(taskId: String)
    fun toggleDeepWork()
    suspend fun getTeamPace(): List<TeamPace>
    suspend fun getGlobalAlerts(): List<GlobalAlert>
}
