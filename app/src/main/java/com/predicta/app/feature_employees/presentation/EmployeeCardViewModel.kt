package com.predicta.app.feature_employees.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predicta.app.data.demo.DemoStateManager
import com.predicta.app.data.demo.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmployeeCardViewModel(
    savedStateHandle: SavedStateHandle,
    private val demoStateManager: DemoStateManager,
) : ViewModel() {

    private val employeeId: String = checkNotNull(savedStateHandle["employeeId"])

    private val _state = MutableStateFlow(EmployeeCardState())
    val state: StateFlow<EmployeeCardState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            demoStateManager.demoState.collect { demo ->
                val isPavel = employeeId == DemoStateManager.PAVEL_ID
                
                if (isPavel) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            employeeId = employeeId,
                            isPavel = true,
                            name = demo.pavelName,
                            role = demo.pavelRole,
                            done = demo.pavelDone,
                            total = demo.pavelTotal,
                            isHealthy = false,
                            predictedDays = demo.pavelPredictedDays,
                            deadlineDays = demo.pavelDeadlineDays,
                            aiInsight = demo.pavelAiInsight,
                            tasks = demo.pavelTasks,
                        )
                    }
                } else {
                    val assignedTasks = demo.pavelTasks.filter { task ->
                        task.assigneeId == DemoStateManager.OLEG_ID && task.status == TaskStatus.REASSIGNED
                    }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            employeeId = employeeId,
                            isPavel = false,
                            name = demo.olegName,
                            role = demo.olegRole,
                            done = demo.olegDone,
                            total = demo.olegTotal,
                            isHealthy = true,
                            tasks = assignedTasks,
                        )
                    }
                }
            }
        }
    }
}
