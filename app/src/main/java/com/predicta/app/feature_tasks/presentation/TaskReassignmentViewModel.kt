package com.predicta.app.feature_tasks.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predicta.app.core.error.AppResult
import com.predicta.app.data.remote.dto.TaskStatus
import com.predicta.app.feature_employees.domain.model.Employee
import com.predicta.app.feature_employees.domain.repository.EmployeeRepository
import com.predicta.app.feature_tasks.domain.usecase.ReassignTaskUseCase
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
    private val employeeRepository: EmployeeRepository,
    private val reassignTaskUseCase: ReassignTaskUseCase,
) : ViewModel() {

    private val taskId: String = checkNotNull(savedStateHandle["taskId"])

    private val _state = MutableStateFlow(TaskReassignmentState())
    val state: StateFlow<TaskReassignmentState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<TaskReassignmentEffect>()
    val effects: SharedFlow<TaskReassignmentEffect> = _effects.asSharedFlow()

    init {
        loadTaskContext()
    }

    private fun loadTaskContext() {
        viewModelScope.launch {
            val employees = when (val result = employeeRepository.getEmployees()) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> {
                    _state.update { it.copy(isLoading = false, canReassign = false) }
                    return@launch
                }
            }

            var taskTitle = ""
            var fromEmployee: Employee? = null
            var canReassign = false

            employees.forEach { employee ->
                val details = when (val result = employeeRepository.getEmployee(employee.id)) {
                    is AppResult.Success -> result.value
                    is AppResult.Failure -> null
                }
                val task = details?.tasks?.firstOrNull { it.id == taskId || it.key == taskId }
                if (task != null) {
                    taskTitle = task.title ?: task.summary ?: task.description ?: taskId
                    fromEmployee = employee
                    canReassign = task.status == TaskStatus.todo || task.status == TaskStatus.in_progress
                }
            }

            val recommended = employees
                .filter { it.id != fromEmployee?.id }
                .minByOrNull { it.burnoutRisk }

            _state.update {
                it.copy(
                    isLoading = false,
                    taskId = if (taskTitle.isNotBlank()) taskId else "",
                    taskTitle = taskTitle,
                    fromName = fromEmployee?.name.orEmpty(),
                    toId = recommended?.id.orEmpty(),
                    toName = recommended?.name.orEmpty(),
                    toRole = recommended?.role.orEmpty(),
                    toDone = recommended?.doneCount ?: 0,
                    toTotal = recommended?.totalCount ?: 0,
                    canReassign = canReassign && recommended != null,
                )
            }
        }
    }

    fun onEvent(event: TaskReassignmentEvent) {
        when (event) {
            is TaskReassignmentEvent.ConfirmReassignment -> confirmReassignment()
            is TaskReassignmentEvent.CompleteReassignment -> {
                viewModelScope.launch {
                    _effects.emit(TaskReassignmentEffect.GoToDashboard)
                }
            }
        }
    }

    private fun confirmReassignment() {
        viewModelScope.launch {
            val current = _state.value
            if (current.toId.isBlank()) return@launch

            when (reassignTaskUseCase(taskId = taskId, newExecutorId = current.toId)) {
                is AppResult.Success -> _state.update { it.copy(isReassigned = true) }
                is AppResult.Failure -> _state.update { it.copy(canReassign = false) }
            }
        }
    }
}

sealed interface TaskReassignmentEffect {
    data object GoToDashboard : TaskReassignmentEffect
}
