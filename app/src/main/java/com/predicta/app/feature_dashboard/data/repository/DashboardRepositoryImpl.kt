package com.predicta.app.feature_dashboard.data.repository

import com.predicta.app.data.demo.DemoStateManager
import com.predicta.app.feature_dashboard.data.mapper.toDomain
import com.predicta.app.feature_dashboard.domain.model.GlobalAlert
import com.predicta.app.feature_dashboard.domain.model.DashboardSnapshot
import com.predicta.app.feature_dashboard.domain.model.TeamPace
import com.predicta.app.feature_dashboard.domain.repository.DashboardRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Mock implementation returning static dummy data.
 * Replace with real API/DB calls once the backend is integrated.
 */
class DashboardRepositoryImpl(
    private val demoStateManager: DemoStateManager,
) : DashboardRepository {

    override fun observeSnapshot(): Flow<DashboardSnapshot> {
        return demoStateManager.demoState.map { it.toDomain() }
    }

    override fun reassignTask(taskId: String) {
        demoStateManager.reassignTask(taskId)
    }

    override fun toggleDeepWork() {
        demoStateManager.toggleDeepWork()
    }

    override suspend fun getTeamPace(): List<TeamPace> {
        delay(400) // Simulate network latency
        return listOf(
            TeamPace(day = "Mon", velocity = 32f),
            TeamPace(day = "Tue", velocity = 45f),
            TeamPace(day = "Wed", velocity = 38f),
            TeamPace(day = "Thu", velocity = 52f),
            TeamPace(day = "Fri", velocity = 48f),
        )
    }

    override suspend fun getGlobalAlerts(): List<GlobalAlert> {
        delay(300) // Simulate network latency
        return listOf(
            GlobalAlert(
                id = "alert_001",
                message = "Team velocity dropping — 15% decline over the last 3 sprints.",
                severity = "high",
            ),
            GlobalAlert(
                id = "alert_002",
                message = "Backend team at risk — workload exceeds sustainable capacity by 22%.",
                severity = "medium",
            ),
        )
    }
}
