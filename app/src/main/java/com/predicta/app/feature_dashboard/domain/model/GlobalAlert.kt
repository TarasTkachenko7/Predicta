package com.predicta.app.feature_dashboard.domain.model

/**
 * Represents an AI-generated alert for the team.
 *
 * @param id Unique identifier for the alert.
 * @param message Human-readable alert description.
 * @param severity One of "high", "medium", or "low".
 */
data class GlobalAlert(
    val id: String,
    val message: String,
    val severity: String,
)
