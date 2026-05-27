package com.predicta.app.feature_dashboard.presentation

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
 * ViewModel for the Dashboard screen.
 */
class DashboardViewModel(
    private val getDemoStateUseCase: GetDemoStateUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<DashboardEffect>()
    val effects: SharedFlow<DashboardEffect> = _effects.asSharedFlow()

    private val dismissedAlertIds = mutableSetOf<String>()
    private var latestSnapshot: DashboardSnapshot? = null

    init {
        observeDemoState()
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.Refresh -> latestSnapshot?.let(::applyDemoState)
            is DashboardEvent.DismissAlert -> dismissAlert(event.alertId)
            is DashboardEvent.AlertClicked -> handleAlertClicked(event.targetId)
            is DashboardEvent.NavigateToTeamVelocity -> {
                viewModelScope.launch {
                    _effects.emit(DashboardEffect.GoToTeamVelocity)
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

    private fun applyDemoState(snapshot: DashboardSnapshot) {
        _state.update { current ->
            reduceDashboardSnapshot(
                currentState = current,
                snapshot = snapshot,
                dismissedAlertIds = dismissedAlertIds,
            )
        }
    }

    private fun dismissAlert(alertId: String) {
        dismissedAlertIds += alertId
        _state.update { currentState -> reduceDismissAlert(currentState, alertId) }
    }

    private fun handleAlertClicked(alertId: String) {
        viewModelScope.launch {
            _effects.emit(DashboardEffect.ResolveAlert(alertId))
        }
    }
}

sealed interface DashboardEffect : UiEffect {
    data object GoToTeamVelocity : DashboardEffect
    data class ResolveAlert(val targetId: String) : DashboardEffect
}
