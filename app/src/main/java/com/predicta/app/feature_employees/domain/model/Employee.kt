package com.predicta.app.feature_employees.domain.model

/**
 * Domain model for a team member.
 *
 * @param id Unique identifier.
 * @param name Full display name.
 * @param role Job title / team role.
 * @param workloadPercentage Current workload as 0.0–1.0 fraction.
 * @param burnoutRisk AI-predicted burnout risk as 0.0–1.0 fraction.
 */
data class Employee(
    val id: String,
    val name: String,
    val role: String,
    val workloadPercentage: Float,
    val burnoutRisk: Float,
)
