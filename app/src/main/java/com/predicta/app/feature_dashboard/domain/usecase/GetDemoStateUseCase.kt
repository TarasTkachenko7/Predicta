package com.predicta.app.feature_dashboard.domain.usecase

import com.predicta.app.feature_dashboard.domain.model.DashboardSnapshot
import com.predicta.app.feature_dashboard.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow

class GetDemoStateUseCase(
    private val repository: DashboardRepository,
) {
    operator fun invoke(): Flow<DashboardSnapshot> {
        return repository.observeSnapshot()
    }
}
