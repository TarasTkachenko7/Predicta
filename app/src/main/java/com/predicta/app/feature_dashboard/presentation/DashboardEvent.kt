package com.predicta.app.feature_dashboard.presentation

/**
 * One-shot events that the UI can send to the ViewModel.
 */
sealed interface DashboardEvent {
    data object Refresh : DashboardEvent
    data class DismissAlert(val alertId: String) : DashboardEvent
}
