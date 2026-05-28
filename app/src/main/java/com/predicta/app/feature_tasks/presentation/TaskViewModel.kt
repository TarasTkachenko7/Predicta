package com.predicta.app.feature_tasks.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predicta.app.core.error.AppResult
import com.predicta.app.core.ui.toUiText
import com.predicta.app.feature_employees.domain.model.Employee
import com.predicta.app.feature_employees.domain.usecase.GetEmployeesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class TaskViewModel(
    private val getEmployees: GetEmployeesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(TaskState())
    val state: StateFlow<TaskState> = _state.asStateFlow()

    init {
        loadEmployees()
    }

    fun onEvent(event: TaskEvent) {
        when (event) {
            is TaskEvent.UpdateDescription -> {
                _state.update { it.copy(taskDescription = event.text) }
                evaluateAiRecommendation()
            }
            is TaskEvent.SelectEmployee -> {
                _state.update {
                    it.copy(
                        selectedEmployee = event.employee,
                        isDropdownExpanded = false,
                    )
                }
                evaluateAiRecommendation()
            }
            is TaskEvent.ToggleDropdown -> {
                _state.update { it.copy(isDropdownExpanded = !it.isDropdownExpanded) }
            }
            is TaskEvent.DismissDropdown -> {
                _state.update { it.copy(isDropdownExpanded = false) }
            }
            is TaskEvent.DismissRecommendation -> {
                _state.update { it.copy(aiRecommendation = null) }
            }
        }
    }

    private fun loadEmployees() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = getEmployees()) {
                is AppResult.Success -> {
                    _state.update { it.copy(isLoading = false, employees = result.value) }
                }
                is AppResult.Failure -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.error.toUiText(),
                        )
                    }
                }
            }
        }
    }

    
    private fun evaluateAiRecommendation() {
        val currentState = _state.value
        val selected = currentState.selectedEmployee
        val description = currentState.taskDescription

        if (selected == null || description.isBlank()) {
            _state.update { it.copy(aiRecommendation = null) }
            return
        }

        if (selected.burnoutRisk >= 0.7f) {
            val alternative = currentState.employees
                .filter { it.id != selected.id }
                .minByOrNull { it.burnoutRisk }

            val burnoutPercent = (selected.burnoutRisk * 100).toInt()
            val recommendation = if (alternative != null) {
                val altPercent = (alternative.burnoutRisk * 100).toInt()
                "⚠\uFE0F ${selected.name} is at ${burnoutPercent}% burnout risk. " +
                    "Reassigning to ${alternative.name} (${altPercent}% risk) balances the workload."
            } else {
                "⚠\uFE0F ${selected.name} is at ${burnoutPercent}% burnout risk. " +
                    "Consider reducing their workload before assigning new tasks."
            }

            _state.update { it.copy(aiRecommendation = recommendation) }
        } else {
            _state.update { it.copy(aiRecommendation = null) }
        }
    }
}

