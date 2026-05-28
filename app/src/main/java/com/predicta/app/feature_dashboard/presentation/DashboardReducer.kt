package com.predicta.app.feature_dashboard.presentation

import com.predicta.app.feature_dashboard.domain.model.DashboardSnapshot
import com.predicta.app.feature_dashboard.domain.model.GlobalAlert
import com.predicta.app.core.ui.formatBackendText

fun reduceDashboardSnapshot(
    currentState: DashboardState,
    snapshot: DashboardSnapshot,
    dismissedAlertIds: Set<String>,
): DashboardState {
    return currentState.copy(
        isLoading = false,
        isRefreshing = false,
        sprintName = snapshot.sprintName,
        isProjectDelayed = snapshot.isProjectDelayed,
        delayDays = snapshot.delayDays,
        delayTrack = snapshot.delayTrack,
        sprintCompletionPercent = snapshot.sprintCompletionPercent,
        sprintElapsedDays = snapshot.sprintElapsedDays,
        sprintTotalDays = snapshot.sprintTotalDays,
        hasBeenReassigned = snapshot.hasBeenReassigned,
        teamPace = snapshot.teamPace,
        alerts = createDashboardAlerts(snapshot)
            .filterNot { alert -> alert.id in dismissedAlertIds },
    )
}

fun reduceDismissAlert(currentState: DashboardState, alertId: String): DashboardState {
    return currentState.copy(
        alerts = currentState.alerts.filter { it.id != alertId },
    )
}

private fun createDashboardAlerts(snapshot: DashboardSnapshot): List<GlobalAlert> {
    return if (snapshot.isProjectDelayed) {
        listOfNotNull(
            GlobalAlert(
                id = "alert_sprint_risk",
                message = "${snapshot.sprintName}. Риск срыва дедлайна " +
                    "${snapshot.delayTrack} на ${snapshot.delayDays} дня.".formatBackendText(),
                severity = "high",
            ),
            snapshot.aiInsight.takeIf { it.isNotBlank() }?.let {
                GlobalAlert(
                    id = "alert_ai_advice",
                    message = it.formatBackendText(),
                    severity = "medium",
                )
            },
        )
    } else {
        emptyList()
    }
}
