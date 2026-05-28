package com.predicta.app.feature_tasks.domain.usecase

import com.predicta.app.feature_dashboard.domain.repository.DashboardRepository
import com.predicta.app.core.error.AppResult

class ReassignTaskUseCase(
    private val repository: DashboardRepository,
) {
    suspend operator fun invoke(taskId: String, newExecutorId: String): AppResult<Unit> {
        return repository.reassignTask(taskId = taskId, newExecutorId = newExecutorId)
    }
}
