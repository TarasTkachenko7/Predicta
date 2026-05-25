package com.predicta.app.feature_dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.predicta.app.data.demo.DemoStateManager
import com.predicta.app.feature_dashboard.domain.model.GlobalAlert
import com.predicta.app.feature_dashboard.domain.model.TeamPace
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
 * Observes [DemoStateManager] for live sprint data and exposes
 * a single [StateFlow]<[DashboardState]>.
 */
class DashboardViewModel(
    private val getDemoStateUseCase: com.predicta.app.feature_dashboard.domain.usecase.GetDemoStateUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private val _navigation = MutableSharedFlow<DashboardNavAction>()
    val navigation: SharedFlow<DashboardNavAction> = _navigation.asSharedFlow()

    private val dismissedAlertIds = mutableSetOf<String>()

    init {
        observeDemoState()
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.Refresh -> applyDemoState(getDemoStateUseCase().value)
            is DashboardEvent.DismissAlert -> dismissAlert(event.alertId)
            is DashboardEvent.AlertClicked -> handleAlertClicked(event.targetId)
            is DashboardEvent.NavigateToTeamVelocity -> {
                viewModelScope.launch {
                    _navigation.emit(DashboardNavAction.GoToTeamVelocity)
                }
            }
        }
    }

    private fun observeDemoState() {
        viewModelScope.launch {
            getDemoStateUseCase().collect { demo ->
                applyDemoState(demo)
            }
        }
    }

    private fun applyDemoState(demo: com.predicta.app.data.demo.DemoData) {
        _state.update {
            it.copy(
                isLoading = false,
                sprintName = demo.sprintName,
                isProjectDelayed = demo.isProjectDelayed,
                delayDays = demo.delayDays,
                delayTrack = demo.delayTrack,
                sprintCompletionPercent = demo.sprintCompletionPercent,
                sprintElapsedDays = demo.sprintElapsedDays,
                sprintTotalDays = demo.sprintTotalDays,
                hasBeenReassigned = demo.hasBeenReassigned,
                teamPace = generatePaceData(demo.isProjectDelayed),
                alerts = generateAlerts(demo)
                    .filterNot { alert -> alert.id in dismissedAlertIds },
            )
        }
    }

    private fun generatePaceData(isDelayed: Boolean): List<TeamPace> {
        return if (isDelayed) {
            listOf(
                TeamPace(day = "Пн", velocity = 8f),
                TeamPace(day = "Вт", velocity = 12f),
                TeamPace(day = "Ср", velocity = 7f),
                TeamPace(day = "Чт", velocity = 5f),
                TeamPace(day = "Пт", velocity = 4f),
                TeamPace(day = "Сб", velocity = 3f),
                TeamPace(day = "Вс", velocity = 2f),
            )
        } else {
            listOf(
                TeamPace(day = "Пн", velocity = 8f),
                TeamPace(day = "Вт", velocity = 12f),
                TeamPace(day = "Ср", velocity = 7f),
                TeamPace(day = "Чт", velocity = 5f),
                TeamPace(day = "Пт", velocity = 9f),
                TeamPace(day = "Сб", velocity = 11f),
                TeamPace(day = "Вс", velocity = 14f),
            )
        }
    }

    private fun generateAlerts(
        demo: com.predicta.app.data.demo.DemoData,
    ): List<GlobalAlert> {
        return if (demo.isProjectDelayed) {
            listOf(
                GlobalAlert(
                    id = "alert_sprint_risk",
                    message = "${demo.sprintName}. Риск срыва дедлайна " +
                        "${demo.delayTrack} на ${demo.delayDays} дня.",
                    severity = "high",
                    triggerSource = "GitHub: 3 ночных коммита",
                ),
                GlobalAlert(
                    id = "alert_pavel_burnout",
                    message = "Критический риск выгорания: ${demo.pavelName} " +
                        "закрыл только ${demo.pavelDone} из ${demo.pavelTotal} задач.",
                    severity = "high",
                    triggerSource = "Calendar: 6 часов созвонов",
                ),
            )
        } else {
            listOf(
                GlobalAlert(
                    id = "alert_resolved",
                    message = "Задача перенаправлена на ${demo.olegName}. " +
                        "Новый прогноз проекта: Сдача вовремя.",
                    severity = "success",
                ),
            )
        }
    }

    private fun dismissAlert(alertId: String) {
        dismissedAlertIds += alertId
        _state.update { currentState ->
            currentState.copy(
                alerts = currentState.alerts.filter { it.id != alertId },
            )
        }
    }

    private fun handleAlertClicked(alertId: String) {
        viewModelScope.launch {
            _navigation.emit(DashboardNavAction.ResolveAlert(alertId))
        }
    }
}

sealed interface DashboardNavAction {
    data object GoToTeamVelocity : DashboardNavAction
    data class ResolveAlert(val targetId: String) : DashboardNavAction
}
