package com.predicta.app.feature_employees.data.repository

import com.predicta.app.core.error.AppResult
import com.predicta.app.core.network.ApiCallExecutor
import com.predicta.app.data.remote.PredictaApi
import com.predicta.app.data.remote.dto.EmployeeAnalyticsDto
import com.predicta.app.data.remote.dto.EmployeeDetailsDto
import com.predicta.app.data.remote.dto.Health
import com.predicta.app.data.remote.dto.TeamVelocityEmployeeDto
import com.predicta.app.feature_employees.domain.model.Employee
import com.predicta.app.feature_employees.domain.repository.EmployeeRepository

class EmployeeRepositoryImpl(
    private val api: PredictaApi,
    private val apiCallExecutor: ApiCallExecutor,
) : EmployeeRepository {

    override suspend fun getEmployees(): AppResult<List<Employee>> {
        return when (val result = apiCallExecutor.execute { api.getTeamVelocity() }) {
            is AppResult.Success -> AppResult.Success(
                enrichEmployeesWithDetails(result.value.map { it.toDomain() }),
            )
            is AppResult.Failure -> result
        }
    }

    override suspend fun getEmployee(id: String): AppResult<EmployeeDetailsDto> {
        return apiCallExecutor.execute { api.getEmployee(id) }
    }

    override suspend fun getEmployeeAnalytics(id: String): AppResult<EmployeeAnalyticsDto> {
        return apiCallExecutor.execute { api.getEmployeeAnalytics(id) }
    }

    private fun TeamVelocityEmployeeDto.toDomain(): Employee {
        val rawWorkload = workloadPercentage ?: workloadPercent ?: workload ?: 0f
        val workloadFraction = if (rawWorkload > 1f) rawWorkload / 100f else rawWorkload

        return Employee(
            id = accountId ?: id.orEmpty(),
            name = name ?: displayName ?: accountId ?: id.orEmpty(),
            role = role ?: position.orEmpty(),
            avatarUrl = avatarUrl,
            workloadPercentage = workloadFraction.coerceIn(0f, 1f),
            burnoutRisk = (burnoutRisk ?: health.toBurnoutRisk()).coerceIn(0f, 1f),
            doneCount = doneCount ?: 0,
            totalCount = totalCount ?: 0,
        )
    }

    private suspend fun enrichEmployeesWithDetails(employees: List<Employee>): List<Employee> {
        return employees.map { employee ->
            if (employee.id.isBlank()) {
                employee
            } else {
                when (val details = apiCallExecutor.execute { api.getEmployee(employee.id) }) {
                    is AppResult.Success -> employee.copy(
                        avatarUrl = details.value.avatarUrl ?: employee.avatarUrl,
                        name = details.value.name ?: details.value.displayName ?: employee.name,
                        role = details.value.role ?: details.value.position ?: employee.role,
                    )
                    is AppResult.Failure -> employee
                }
            }
        }
    }

    private fun Health?.toBurnoutRisk(): Float {
        return when (this) {
            Health.good -> 0.15f
            Health.normal -> 0.45f
            Health.bad -> 0.85f
            null -> 0f
        }
    }
}
