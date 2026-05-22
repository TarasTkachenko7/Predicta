package com.predicta.app.feature_dashboard.domain.repository

import com.predicta.app.feature_dashboard.domain.model.GlobalAlert
import com.predicta.app.feature_dashboard.domain.model.TeamPace

/**
 * Repository contract for dashboard data.
 * Implemented by [com.predicta.app.feature_dashboard.data.repository.DashboardRepositoryImpl].
 */
interface DashboardRepository {
    suspend fun getTeamPace(): List<TeamPace>
    suspend fun getGlobalAlerts(): List<GlobalAlert>
}
