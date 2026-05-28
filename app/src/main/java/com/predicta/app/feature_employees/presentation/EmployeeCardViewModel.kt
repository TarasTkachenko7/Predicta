package com.predicta.app.feature_employees.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predicta.app.core.error.AppResult
import com.predicta.app.data.remote.dto.EmployeeAnalyticsDto
import com.predicta.app.data.remote.dto.EmployeeDetailsDto
import com.predicta.app.data.remote.dto.Health
import com.predicta.app.data.remote.dto.TaskDto
import com.predicta.app.data.remote.dto.TaskStatus
import com.predicta.app.core.ui.formatBackendText
import com.predicta.app.feature_dashboard.domain.model.DashboardTask
import com.predicta.app.feature_dashboard.domain.model.DashboardTaskStatus
import com.predicta.app.feature_employees.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmployeeCardViewModel(
    savedStateHandle: SavedStateHandle,
    private val employeeRepository: EmployeeRepository,
) : ViewModel() {

    private val employeeId: String = checkNotNull(savedStateHandle["employeeId"])

    private val _state = MutableStateFlow(EmployeeCardState())
    val state: StateFlow<EmployeeCardState> = _state.asStateFlow()

    init {
        loadEmployee()
    }

    private fun loadEmployee() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val details = when (val result = employeeRepository.getEmployee(employeeId)) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> {
                    _state.update { it.copy(isLoading = false) }
                    return@launch
                }
            }

            val analytics = when (val result = employeeRepository.getEmployeeAnalytics(employeeId)) {
                is AppResult.Success -> result.value
                is AppResult.Failure -> null
            }

            _state.update {
                details.toState(
                    employeeId = employeeId,
                    analytics = analytics,
                    currentState = it,
                )
            }
        }
    }

    fun onToggleDeepWork() = Unit

    fun onToggleChartMode() = Unit

    private fun EmployeeDetailsDto.toState(
        employeeId: String,
        analytics: EmployeeAnalyticsDto?,
        currentState: EmployeeCardState,
    ): EmployeeCardState {
        val completed = analytics?.completedTasks ?: doneCount ?: tasks.count { it.status == TaskStatus.done }
        val total = analytics?.totalTasks ?: totalCount ?: tasks.size
        val predicted = analytics?.forecastDaysToComplete ?: analytics?.predictedDays ?: 0
        val deadline = analytics?.sprintDaysLeft ?: analytics?.deadlineDays ?: 0
        val resolvedHealth = analytics?.health ?: health

        return currentState.copy(
            isLoading = false,
            employeeId = employeeId,
            showAnalytics = true,
            name = analytics?.employeeName ?: name ?: displayName ?: employeeId,
            role = analytics?.role ?: role ?: position.orEmpty(),
            avatarUrl = avatarUrl,
            done = completed,
            total = total,
            isHealthy = resolvedHealth != Health.bad,
            predictedDays = predicted,
            deadlineDays = deadline,
            aiInsight = (analytics?.aiInsight ?: aiInsight.orEmpty()).formatBackendText(),
            riskFactors = analytics?.riskFactors.orEmpty().map { it.formatBackendText() },
            tasks = (analytics?.tasks?.takeIf { it.isNotEmpty() } ?: tasks).map { it.toDomain(employeeId) },
            isDeepWorkActive = false,
        )
    }

    private fun TaskDto.toDomain(employeeId: String): DashboardTask {
        return DashboardTask(
            id = id ?: key.orEmpty(),
            title = title ?: summary ?: description ?: id ?: key.orEmpty(),
            status = status.toDomain(),
            assigneeId = assigneeId ?: employeeId,
            assigneeName = assigneeName.orEmpty(),
        )
    }

    private fun TaskStatus?.toDomain(): DashboardTaskStatus {
        return when (this) {
            TaskStatus.todo -> DashboardTaskStatus.TODO
            TaskStatus.in_progress -> DashboardTaskStatus.IN_PROGRESS
            TaskStatus.done -> DashboardTaskStatus.DONE
            null -> DashboardTaskStatus.TODO
        }
    }
}
