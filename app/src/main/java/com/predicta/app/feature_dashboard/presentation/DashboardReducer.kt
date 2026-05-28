package com.predicta.app.feature_dashboard.presentation

import com.predicta.app.feature_dashboard.domain.model.DashboardSnapshot
import com.predicta.app.feature_dashboard.domain.model.GlobalAlert
import com.predicta.app.feature_dashboard.domain.model.TeamPace

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
        teamPace = createTeamPace(snapshot),
        alerts = createDashboardAlerts(snapshot)
            .filterNot { alert -> alert.id in dismissedAlertIds },
    )
}

fun reduceDismissAlert(currentState: DashboardState, alertId: String): DashboardState {
    return currentState.copy(
        alerts = currentState.alerts.filter { it.id != alertId },
    )
}

private fun createTeamPace(snapshot: DashboardSnapshot): List<TeamPace> {
    return listOf(
        TeamPace(day = snapshot.primaryEmployeeName.ifBlank { "1" }, velocity = snapshot.primaryEmployeeDone.toFloat()),
        TeamPace(day = snapshot.secondaryEmployeeName.ifBlank { "2" }, velocity = snapshot.secondaryEmployeeDone.toFloat()),
    ).filter { it.day.isNotBlank() }
}

private fun createDashboardAlerts(snapshot: DashboardSnapshot): List<GlobalAlert> {
    return if (snapshot.isProjectDelayed) {
        listOfNotNull(
            GlobalAlert(
                id = "alert_sprint_risk",
                message = "${snapshot.sprintName}. Риск срыва дедлайна " +
                    "${snapshot.delayTrack} на ${snapshot.delayDays} дня.",
                severity = "high",
            ),
            snapshot.aiInsight.takeIf { it.isNotBlank() }?.let {
                GlobalAlert(
                    id = "alert_ai_advice",
                    message = it,
                    severity = "medium",
                )
            },
        )
    } else {
        emptyList()
    }
}
