package com.predicta.app.feature_employees.domain.usecase

import com.predicta.app.feature_dashboard.domain.repository.DashboardRepository

class ToggleDeepWorkUseCase(
    private val repository: DashboardRepository,
) {
    operator fun invoke() {
    }
}
