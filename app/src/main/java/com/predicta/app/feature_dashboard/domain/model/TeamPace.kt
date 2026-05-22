package com.predicta.app.feature_dashboard.domain.model

/**
 * Represents a single day's sprint velocity measurement for the team.
 */
data class TeamPace(
    val day: String,
    val velocity: Float,
)
