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
    private val employeeDetailsCache = mutableMapOf<String, EmployeeDetailsDto>()

    override suspend fun getEmployees(): AppResult<List<Employee>> {
        return when (val result = apiCallExecutor.execute { api.getTeamVelocity() }) {
            is AppResult.Success -> AppResult.Success(
                enrichEmployeesWithDetails(result.value.map { it.toDomain() }),
            )
            is AppResult.Failure -> result
        }
    }

    override suspend fun getEmployee(id: String): AppResult<EmployeeDetailsDto> {
        employeeDetailsCache[id]?.let { return AppResult.Success(it) }

        return when (val result = apiCallExecutor.execute { api.getEmployee(id) }) {
            is AppResult.Success -> {
                employeeDetailsCache[id] = result.value
                result
            }
            is AppResult.Failure -> result
        }
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
            val cachedDetails = employeeDetailsCache[employee.id]
            if (cachedDetails != null) {
                employee.withDetails(cachedDetails)
            } else if (employee.id.isBlank()) {
                employee
            } else {
                when (val details = apiCallExecutor.execute { api.getEmployee(employee.id) }) {
                    is AppResult.Success -> {
                        employeeDetailsCache[employee.id] = details.value
                        employee.withDetails(details.value)
                    }
                    is AppResult.Failure -> employee
                }
            }
        }
    }

    private fun Employee.withDetails(details: EmployeeDetailsDto): Employee {
        return copy(
            avatarUrl = details.avatarUrl ?: avatarUrl,
            name = details.name ?: details.displayName ?: name,
            role = details.role ?: details.position ?: role,
        )
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
