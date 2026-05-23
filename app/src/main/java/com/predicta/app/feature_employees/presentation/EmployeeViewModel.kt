package com.predicta.app.feature_employees.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predicta.app.data.demo.DemoStateManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Team Velocity screen.
 * Observes [DemoStateManager] for live data about Oleg and Pavel.
 */
class EmployeeViewModel(
    private val demoStateManager: DemoStateManager,
) : ViewModel() {

    private val _state = MutableStateFlow(EmployeeState())
    val state: StateFlow<EmployeeState> = _state.asStateFlow()

    private val _navigation = MutableSharedFlow<EmployeeNavAction>()
    val navigation: SharedFlow<EmployeeNavAction> = _navigation.asSharedFlow()

    init {
        observeDemoState()
    }

    fun onEvent(event: EmployeeEvent) {
        when (event) {
            is EmployeeEvent.Refresh -> applyDemoState(demoStateManager.demoState.value)
            is EmployeeEvent.SelectEmployee -> {
                viewModelScope.launch {
                    _navigation.emit(
                        EmployeeNavAction.GoToEmployeeCard(event.employeeId),
                    )
                }
            }
        }
    }

    private fun observeDemoState() {
        viewModelScope.launch {
            demoStateManager.demoState.collect { demo ->
                applyDemoState(demo)
            }
        }
    }

    private fun applyDemoState(demo: com.predicta.app.data.demo.DemoData) {
        _state.update {
            it.copy(
                isLoading = false,
                demoData = demo,
            )
        }
    }
}

sealed interface EmployeeNavAction {
    data class GoToEmployeeCard(val employeeId: String) : EmployeeNavAction
}
