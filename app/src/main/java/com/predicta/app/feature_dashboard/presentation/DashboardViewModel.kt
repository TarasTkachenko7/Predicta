package com.predicta.app.feature_dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predicta.app.feature_dashboard.domain.usecase.GetDashboardDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Dashboard screen.
 * Exposes a single [StateFlow]<[DashboardState]> and accepts events via [onEvent].
 */
class DashboardViewModel(
    private val getDashboardData: GetDashboardDataUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadDashboard()
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.Refresh -> loadDashboard()
            is DashboardEvent.DismissAlert -> dismissAlert(event.alertId)
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val data = getDashboardData()
                _state.update {
                    it.copy(
                        isLoading = false,
                        teamPace = data.teamPace,
                        alerts = data.alerts,
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "An unexpected error occurred.",
                    )
                }
            }
        }
    }

    private fun dismissAlert(alertId: String) {
        _state.update { currentState ->
            currentState.copy(
                alerts = currentState.alerts.filter { it.id != alertId },
            )
        }
    }
}
