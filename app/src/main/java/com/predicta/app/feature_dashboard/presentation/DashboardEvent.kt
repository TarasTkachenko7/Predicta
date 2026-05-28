package com.predicta.app.feature_dashboard.presentation


sealed interface DashboardEvent {
    data object Refresh : DashboardEvent
    data class DismissAlert(val alertId: String) : DashboardEvent
    data class AlertClicked(val targetId: String) : DashboardEvent
    data object NavigateToTeamVelocity : DashboardEvent
}

