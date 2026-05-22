package com.predicta.app.feature_dashboard.domain.usecase

import com.predicta.app.feature_dashboard.domain.model.GlobalAlert
import com.predicta.app.feature_dashboard.domain.model.TeamPace
import com.predicta.app.feature_dashboard.domain.repository.DashboardRepository

/**
 * Single-responsibility use case that fetches all dashboard data in one shot.
 */
class GetDashboardDataUseCase(
    private val repository: DashboardRepository,
) {

    data class DashboardData(
        val teamPace: List<TeamPace>,
        val alerts: List<GlobalAlert>,
    )

    suspend operator fun invoke(): DashboardData {
        return DashboardData(
            teamPace = repository.getTeamPace(),
            alerts = repository.getGlobalAlerts(),
        )
    }
}
