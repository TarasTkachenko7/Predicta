package com.predicta.app.feature_employees.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predicta.app.core.ui.UiEffect
import com.predicta.app.feature_dashboard.domain.model.DashboardSnapshot
import com.predicta.app.feature_dashboard.domain.usecase.GetDemoStateUseCase
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
    private val getDemoStateUseCase: GetDemoStateUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(EmployeeState())
    val state: StateFlow<EmployeeState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<EmployeeEffect>()
    val effects: SharedFlow<EmployeeEffect> = _effects.asSharedFlow()
    private var latestSnapshot: DashboardSnapshot? = null

    init {
        observeDemoState()
    }

    fun onEvent(event: EmployeeEvent) {
        when (event) {
            is EmployeeEvent.Refresh -> latestSnapshot?.let(::applyDemoState)
            is EmployeeEvent.SelectEmployee -> {
                viewModelScope.launch {
                    _effects.emit(
                        EmployeeEffect.GoToEmployeeCard(event.employeeId),
                    )
                }
            }
        }
    }

    private fun observeDemoState() {
        viewModelScope.launch {
            getDemoStateUseCase().collect { demo ->
                latestSnapshot = demo
                applyDemoState(demo)
            }
        }
    }

    private fun applyDemoState(demo: DashboardSnapshot) {
        _state.update {
            it.copy(
                isLoading = false,
                demoData = demo,
            )
        }
    }
}

sealed interface EmployeeEffect : UiEffect {
    data class GoToEmployeeCard(val employeeId: String) : EmployeeEffect
}
