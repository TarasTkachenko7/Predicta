package com.predicta.app.feature_tasks.domain.usecase

import com.predicta.app.feature_dashboard.domain.repository.DashboardRepository

class ReassignTaskUseCase(
    private val repository: DashboardRepository,
) {
    operator fun invoke(taskId: String) {
        repository.reassignTask(taskId)
    }
}
