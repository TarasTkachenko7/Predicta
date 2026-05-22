package com.predicta.app.feature_employees.data.repository

import com.predicta.app.feature_employees.domain.model.Employee
import com.predicta.app.feature_employees.domain.repository.EmployeeRepository
import kotlinx.coroutines.delay

/**
 * Mock implementation returning static employee data.
 * Replace with real API calls once the Golang backend is integrated.
 */
class EmployeeRepositoryImpl : EmployeeRepository {

    override suspend fun getEmployees(): List<Employee> {
        delay(350) // Simulate network latency
        return listOf(
            Employee(
                id = "emp_001",
                name = "Alex Rivera",
                role = "Backend Engineer",
                workloadPercentage = 0.92f,
                burnoutRisk = 0.85f,
            ),
            Employee(
                id = "emp_002",
                name = "Sarah Chen",
                role = "Frontend Engineer",
                workloadPercentage = 0.55f,
                burnoutRisk = 0.22f,
            ),
            Employee(
                id = "emp_003",
                name = "Jordan Kim",
                role = "Product Designer",
                workloadPercentage = 0.74f,
                burnoutRisk = 0.58f,
            ),
        )
    }
}
