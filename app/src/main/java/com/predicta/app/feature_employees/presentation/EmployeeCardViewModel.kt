package com.predicta.app.feature_employees.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predicta.app.feature_dashboard.domain.model.DashboardEmployeeIds
import com.predicta.app.feature_dashboard.domain.model.DashboardTaskStatus
import com.predicta.app.feature_dashboard.domain.usecase.GetDemoStateUseCase
import com.predicta.app.feature_employees.domain.usecase.ToggleDeepWorkUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmployeeCardViewModel(
    savedStateHandle: SavedStateHandle,
    private val getDemoStateUseCase: GetDemoStateUseCase,
    private val toggleDeepWorkUseCase: ToggleDeepWorkUseCase,
) : ViewModel() {

    private val employeeId: String = checkNotNull(savedStateHandle["employeeId"])

    private val _state = MutableStateFlow(EmployeeCardState())
    val state: StateFlow<EmployeeCardState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getDemoStateUseCase().collect { demo ->
                val isPavel = employeeId == DashboardEmployeeIds.PAVEL_ID
                
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
                            riskFactors = demo.pavelRiskFactors,
                            tasks = demo.pavelTasks,
                            isDeepWorkActive = demo.isDeepWorkActive,
                        )
                    }
                } else {
                    val assignedTasks = demo.pavelTasks.filter { task ->
                        task.assigneeId == DashboardEmployeeIds.OLEG_ID &&
                            task.status == DashboardTaskStatus.REASSIGNED
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
                            isDeepWorkActive = demo.isDeepWorkActive,
                        )
                    }
                }
            }
        }
    }

    fun onToggleDeepWork() {
        toggleDeepWorkUseCase()
    }

    fun onToggleChartMode() {
        _state.update { current ->
            val newShowRecovery = !current.showRecoveryForecast
            val newForecastData = if (newShowRecovery) {
                listOf(0.85f, 0.70f, 0.55f, 0.40f, 0.30f, 0.25f, 0.20f)
            } else {
                emptyList()
            }
            current.copy(
                showRecoveryForecast = newShowRecovery,
                recoveryForecastData = newForecastData,
            )
        }
    }
}
