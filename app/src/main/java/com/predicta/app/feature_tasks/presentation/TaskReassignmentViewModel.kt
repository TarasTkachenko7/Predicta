package com.predicta.app.feature_tasks.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predicta.app.data.demo.DemoStateManager
import com.predicta.app.data.demo.TaskStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskReassignmentViewModel(
    savedStateHandle: SavedStateHandle,
    private val getDemoStateUseCase: com.predicta.app.feature_dashboard.domain.usecase.GetDemoStateUseCase,
    private val reassignTaskUseCase: com.predicta.app.feature_tasks.domain.usecase.ReassignTaskUseCase,
) : ViewModel() {

    private val taskId: String = checkNotNull(savedStateHandle["taskId"])

    private val _state = MutableStateFlow(TaskReassignmentState())
    val state: StateFlow<TaskReassignmentState> = _state.asStateFlow()

    private val _navigation = MutableSharedFlow<TaskReassignmentNavAction>()
    val navigation: SharedFlow<TaskReassignmentNavAction> = _navigation.asSharedFlow()

    init {
        viewModelScope.launch {
            getDemoStateUseCase().collect { demo ->
                val task = demo.pavelTasks.find { it.id == taskId }
                if (task != null) {
                    val canReassign = task.status == TaskStatus.IN_PROGRESS || task.status == TaskStatus.TODO
                    _state.update {
                        it.copy(
                            isLoading = false,
                            taskId = task.id,
                            taskTitle = task.title,
                            fromName = demo.pavelName,
                            toName = demo.olegName,
                            toRole = demo.olegRole,
                            toDone = demo.olegDone,
                            toTotal = demo.olegTotal,
                            canReassign = canReassign,
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false, canReassign = false) }
                }
            }
        }
    }

    fun onEvent(event: TaskReassignmentEvent) {
        when (event) {
            is TaskReassignmentEvent.ConfirmReassignment -> {
                reassignTaskUseCase(taskId)
                _state.update { it.copy(isReassigned = true) }
            }
            is TaskReassignmentEvent.CompleteReassignment -> {
                viewModelScope.launch {
                    _navigation.emit(TaskReassignmentNavAction.GoToDashboard)
                }
            }
        }
    }
}
