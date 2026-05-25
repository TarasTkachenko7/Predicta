package com.predicta.app.feature_tasks.domain.usecase

import com.predicta.app.data.demo.DemoStateManager

class ReassignTaskUseCase(
    private val demoStateManager: DemoStateManager
) {
    operator fun invoke(taskId: String) {
        demoStateManager.reassignTask(taskId)
    }
}
