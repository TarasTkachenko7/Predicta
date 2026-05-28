package com.predicta.app.feature_dashboard.domain.model


data class GlobalAlert(
    val id: String,
    val message: String,
    val severity: String,
    val triggerSource: String? = null,
)

