package com.predicta.app.feature_employees.domain.usecase

import com.predicta.app.feature_employees.domain.model.Employee
import com.predicta.app.feature_employees.domain.repository.EmployeeRepository

/**
 * Single-responsibility use case for fetching the employee list.
 */
class GetEmployeesUseCase(
    private val repository: EmployeeRepository,
) {
    suspend operator fun invoke(): List<Employee> {
        return repository.getEmployees()
    }
}
