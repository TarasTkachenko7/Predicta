package com.predicta.app.feature_employees.domain.usecase

import com.predicta.app.core.error.AppResult
import com.predicta.app.feature_employees.domain.model.Employee
import com.predicta.app.feature_employees.domain.repository.EmployeeRepository


class GetEmployeesUseCase(
    private val repository: EmployeeRepository,
) {
    suspend operator fun invoke(): AppResult<List<Employee>> {
        return repository.getEmployees()
    }
}

