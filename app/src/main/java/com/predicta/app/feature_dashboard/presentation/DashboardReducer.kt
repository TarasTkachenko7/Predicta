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
        sprintName = snapshot.sprintName,
        isProjectDelayed = snapshot.isProjectDelayed,
        delayDays = snapshot.delayDays,
        delayTrack = snapshot.delayTrack,
        sprintCompletionPercent = snapshot.sprintCompletionPercent,
        sprintElapsedDays = snapshot.sprintElapsedDays,
        sprintTotalDays = snapshot.sprintTotalDays,
        hasBeenReassigned = snapshot.hasBeenReassigned,
        teamPace = createTeamPace(snapshot.isProjectDelayed),
        alerts = createDashboardAlerts(snapshot)
            .filterNot { alert -> alert.id in dismissedAlertIds },
    )
}

fun reduceDismissAlert(currentState: DashboardState, alertId: String): DashboardState {
    return currentState.copy(
        alerts = currentState.alerts.filter { it.id != alertId },
    )
}

private fun createTeamPace(isDelayed: Boolean): List<TeamPace> {
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

private fun createDashboardAlerts(snapshot: DashboardSnapshot): List<GlobalAlert> {
    return if (snapshot.isProjectDelayed) {
        listOf(
            GlobalAlert(
                id = "alert_sprint_risk",
                message = "${snapshot.sprintName}. Риск срыва дедлайна " +
                    "${snapshot.delayTrack} на ${snapshot.delayDays} дня.",
                severity = "high",
                triggerSource = "GitHub: 3 ночных коммита",
            ),
            GlobalAlert(
                id = "alert_pavel_burnout",
                message = "Критический риск выгорания: ${snapshot.pavelName} " +
                    "закрыл только ${snapshot.pavelDone} из ${snapshot.pavelTotal} задач.",
                severity = "high",
                triggerSource = "Calendar: 6 часов созвонов",
            ),
        )
    } else {
        listOf(
            GlobalAlert(
                id = "alert_resolved",
                message = "Задача перенаправлена на ${snapshot.olegName}. " +
                    "Новый прогноз проекта: Сдача вовремя.",
                severity = "success",
            ),
        )
    }
}
