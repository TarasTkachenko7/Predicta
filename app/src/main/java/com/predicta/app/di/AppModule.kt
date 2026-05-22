package com.predicta.app.di

import com.predicta.app.feature_dashboard.data.repository.DashboardRepositoryImpl
import com.predicta.app.feature_dashboard.domain.repository.DashboardRepository
import com.predicta.app.feature_dashboard.domain.usecase.GetDashboardDataUseCase
import com.predicta.app.feature_dashboard.presentation.DashboardViewModel
import com.predicta.app.feature_employees.data.repository.EmployeeRepositoryImpl
import com.predicta.app.feature_employees.domain.repository.EmployeeRepository
import com.predicta.app.feature_employees.domain.usecase.GetEmployeesUseCase
import com.predicta.app.feature_employees.presentation.EmployeeViewModel
import com.predicta.app.feature_tasks.presentation.TaskViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Top-level application module for Koin dependency injection.
 *
 * Feature-specific repositories, use-cases, and ViewModels are registered here.
 * As the app grows, split into per-feature modules if this file gets unwieldy.
 */
val appModule = module {

    // ── feature_dashboard ───────────────────────────────────────────────
    single<DashboardRepository> { DashboardRepositoryImpl() }
    factory { GetDashboardDataUseCase(repository = get()) }
    viewModel { DashboardViewModel(getDashboardData = get()) }

    // ── feature_employees ───────────────────────────────────────────────
    single<EmployeeRepository> { EmployeeRepositoryImpl() }
    factory { GetEmployeesUseCase(repository = get()) }
    viewModel { EmployeeViewModel(getEmployees = get()) }

    // ── feature_tasks ───────────────────────────────────────────────────
    // TaskViewModel reuses GetEmployeesUseCase for the assignee dropdown
    viewModel { TaskViewModel(getEmployees = get()) }
}
