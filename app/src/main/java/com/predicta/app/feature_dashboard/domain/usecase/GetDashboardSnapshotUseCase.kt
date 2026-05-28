package com.predicta.app.feature_dashboard.domain.usecase

import com.predicta.app.core.error.AppResult
import com.predicta.app.feature_dashboard.domain.model.DashboardSnapshot
import com.predicta.app.feature_dashboard.domain.repository.DashboardRepository

class GetDashboardSnapshotUseCase(
    private val repository: DashboardRepository,
) {
    suspend operator fun invoke(): AppResult<DashboardSnapshot> {
        return repository.getSnapshot()
    }
}
