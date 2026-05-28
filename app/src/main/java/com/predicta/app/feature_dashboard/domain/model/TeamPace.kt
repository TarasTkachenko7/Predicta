package com.predicta.app.feature_dashboard.domain.model

data class TeamPace(
    val day: String,
    val completedCount: Int,
    val totalCount: Int,
    val isRisky: Boolean = false,
)
