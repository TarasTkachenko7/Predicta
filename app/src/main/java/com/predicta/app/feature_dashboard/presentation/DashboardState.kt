package com.predicta.app.feature_dashboard.presentation

import com.predicta.app.feature_dashboard.domain.model.GlobalAlert
import com.predicta.app.feature_dashboard.domain.model.TeamPace

/**
 * Immutable UI state for the Dashboard screen.
 */
data class DashboardState(
    val isLoading: Boolean = true,
    val teamPace: List<TeamPace> = emptyList(),
    val alerts: List<GlobalAlert> = emptyList(),
    val error: String? = null,
)
