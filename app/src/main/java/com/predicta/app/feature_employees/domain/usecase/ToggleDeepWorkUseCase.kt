package com.predicta.app.feature_employees.domain.usecase

import com.predicta.app.data.demo.DemoStateManager

class ToggleDeepWorkUseCase(
    private val demoStateManager: DemoStateManager
) {
    operator fun invoke() {
        demoStateManager.toggleDeepWork()
    }
}
