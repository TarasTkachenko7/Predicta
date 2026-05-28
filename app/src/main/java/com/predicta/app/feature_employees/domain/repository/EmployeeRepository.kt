package com.predicta.app.feature_employees.domain.repository

import com.predicta.app.core.error.AppResult
import com.predicta.app.data.remote.dto.EmployeeAnalyticsDto
import com.predicta.app.data.remote.dto.EmployeeDetailsDto
import com.predicta.app.feature_employees.domain.model.Employee

interface EmployeeRepository {
    suspend fun getEmployees(): AppResult<List<Employee>>
    suspend fun getEmployee(id: String): AppResult<EmployeeDetailsDto>
    suspend fun getEmployeeAnalytics(id: String): AppResult<EmployeeAnalyticsDto>
}
