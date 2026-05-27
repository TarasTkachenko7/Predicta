package com.predicta.app.feature_employees.domain.repository

import com.predicta.app.core.error.AppResult
import com.predicta.app.feature_employees.domain.model.Employee

/**
 * Repository contract for employee data.
 */
interface EmployeeRepository {
    suspend fun getEmployees(): AppResult<List<Employee>>
}
