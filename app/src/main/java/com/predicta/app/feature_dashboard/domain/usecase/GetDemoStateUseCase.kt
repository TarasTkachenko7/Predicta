package com.predicta.app.feature_dashboard.domain.usecase

import com.predicta.app.data.demo.DemoData
import com.predicta.app.data.demo.DemoStateManager
import kotlinx.coroutines.flow.StateFlow

class GetDemoStateUseCase(
    private val demoStateManager: DemoStateManager
) {
    operator fun invoke(): StateFlow<DemoData> {
        return demoStateManager.demoState
    }
}
