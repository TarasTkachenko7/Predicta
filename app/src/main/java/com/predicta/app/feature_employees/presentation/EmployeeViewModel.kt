package com.predicta.app.feature_employees.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predicta.app.feature_employees.domain.usecase.GetEmployeesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Employee Details screen.
 * Exposes a single [StateFlow]<[EmployeeState]> and accepts events via [onEvent].
 */
class EmployeeViewModel(
    private val getEmployees: GetEmployeesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(EmployeeState())
    val state: StateFlow<EmployeeState> = _state.asStateFlow()

    init {
        loadEmployees()
    }

    fun onEvent(event: EmployeeEvent) {
        when (event) {
            is EmployeeEvent.Refresh -> loadEmployees()
        }
    }

    private fun loadEmployees() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val employees = getEmployees()
                _state.update {
                    it.copy(isLoading = false, employees = employees)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load employees.",
                    )
                }
            }
        }
    }
}
