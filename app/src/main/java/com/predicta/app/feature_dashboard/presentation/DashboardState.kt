package com.predicta.app.feature_dashboard.presentation

import com.predicta.app.feature_dashboard.domain.model.GlobalAlert
import com.predicta.app.feature_dashboard.domain.model.TeamPace

/**
 * Immutable UI state for the Dashboard screen (Sprint Health).
 */
data class DashboardState(
    val isLoading: Boolean = true,
    val sprintName: String = "",
    val isProjectDelayed: Boolean = false,
    val delayDays: Int = 0,
    val delayTrack: String = "",
    val sprintCompletionPercent: Float = 0f,
    val sprintElapsedDays: Int = 0,
    val sprintTotalDays: Int = 0,
    val hasBeenReassigned: Boolean = false,
    val teamPace: List<TeamPace> = emptyList(),
    val alerts: List<GlobalAlert> = emptyList(),
    val error: String? = null,
)
