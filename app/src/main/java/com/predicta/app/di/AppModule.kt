package com.predicta.app.di

import com.predicta.app.core.network.NetworkMonitor
import com.predicta.app.feature_auth.data.repository.AuthRepositoryImpl
import com.predicta.app.feature_auth.data.session.UserSessionManager
import com.predicta.app.feature_auth.domain.repository.AuthRepository
import com.predicta.app.feature_auth.domain.usecase.AuthInteractors
import com.predicta.app.feature_auth.domain.usecase.LoginUseCase
import com.predicta.app.feature_auth.domain.usecase.RegisterUseCase
import com.predicta.app.feature_auth.presentation.AuthViewModel
import com.predicta.app.feature_dashboard.data.repository.DashboardRepositoryImpl
import com.predicta.app.feature_dashboard.domain.repository.DashboardRepository
import com.predicta.app.feature_dashboard.domain.usecase.GetDashboardSnapshotUseCase
import com.predicta.app.feature_dashboard.presentation.DashboardViewModel
import com.predicta.app.feature_employees.data.repository.EmployeeRepositoryImpl
import com.predicta.app.feature_employees.domain.repository.EmployeeRepository
import com.predicta.app.feature_employees.domain.usecase.GetEmployeesUseCase
import com.predicta.app.feature_employees.presentation.EmployeeCardViewModel
import com.predicta.app.feature_employees.presentation.EmployeeViewModel
import com.predicta.app.feature_settings.data.repository.AppSettingsRepository
import com.predicta.app.feature_settings.presentation.SettingsViewModel
import com.predicta.app.feature_tasks.domain.usecase.ReassignTaskUseCase
import com.predicta.app.feature_tasks.presentation.TaskReassignmentViewModel
import com.predicta.app.feature_tasks.presentation.TaskViewModel
import com.predicta.app.ui.AppViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { NetworkMonitor(androidContext()) }
    single { UserSessionManager(androidContext()) }
    viewModel { AppViewModel(networkMonitor = get(), sessionManager = get()) }
    single<AuthRepository> {
        AuthRepositoryImpl(
            api = get(),
            apiCallExecutor = get(),
            sessionManager = get(),
        )
    }
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { AuthInteractors(login = get(), register = get()) }
    viewModel { 
        AuthViewModel(
            interactors = get(),
            sessionManager = get()
        ) 
    }
    single { AppSettingsRepository(androidContext()) }
    single<DashboardRepository> { DashboardRepositoryImpl(api = get(), apiCallExecutor = get()) }
    factory { GetDashboardSnapshotUseCase(repository = get()) }
    viewModel { DashboardViewModel(getDashboardSnapshotUseCase = get()) }
    single<EmployeeRepository> { EmployeeRepositoryImpl(api = get(), apiCallExecutor = get()) }
    factory { GetEmployeesUseCase(repository = get()) }
    viewModel { EmployeeViewModel(getEmployeesUseCase = get()) }
    viewModel { EmployeeCardViewModel(savedStateHandle = get(), employeeRepository = get()) }
    factory { ReassignTaskUseCase(repository = get()) }
    viewModel { TaskViewModel(getEmployees = get()) }
    viewModel {
        TaskReassignmentViewModel(
            savedStateHandle = get(),
            employeeRepository = get(),
            reassignTaskUseCase = get(),
        )
    }
    viewModel {
        SettingsViewModel(
            settingsRepository = get(),
            sessionManager = get(),
            baseUrlProvider = get(),
        )
    }
}

