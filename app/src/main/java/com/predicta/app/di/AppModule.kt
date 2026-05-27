package com.predicta.app.di

import com.predicta.app.core.network.NetworkMonitor
import com.predicta.app.data.demo.DemoStateManager
import com.predicta.app.feature_auth.data.repository.AuthRepositoryImpl
import com.predicta.app.feature_auth.data.session.UserSessionManager
import com.predicta.app.feature_auth.domain.repository.AuthRepository
import com.predicta.app.feature_auth.domain.usecase.AuthInteractors
import com.predicta.app.feature_auth.domain.usecase.LoginUseCase
import com.predicta.app.feature_auth.domain.usecase.RegisterUseCase
import com.predicta.app.feature_auth.domain.usecase.ResetPasswordUseCase
import com.predicta.app.feature_auth.presentation.AuthViewModel
import com.predicta.app.feature_dashboard.data.repository.DashboardRepositoryImpl
import com.predicta.app.feature_dashboard.domain.repository.DashboardRepository
import com.predicta.app.feature_dashboard.domain.usecase.GetDemoStateUseCase
import com.predicta.app.feature_dashboard.presentation.DashboardViewModel
import com.predicta.app.feature_employees.domain.usecase.ToggleDeepWorkUseCase
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

/**
 * Top-level application module for Koin dependency injection.
 *
 * Registers the [DemoStateManager] singleton and feature ViewModels.
 * The DemoStateManager acts as a single source of truth for the
 * hackathon demo scenario, simulating backend state changes.
 */
val appModule = module {

    // ── Demo State Manager (shared singleton) ───────────────────────────
    single { DemoStateManager() }

    // ── App Runtime ─────────────────────────────────────────────────────
    single { NetworkMonitor(androidContext()) }
    single { UserSessionManager(androidContext()) }
    viewModel { AppViewModel(networkMonitor = get(), sessionManager = get()) }

    // ── Auth Feature ────────────────────────────────────────────────────
    single<AuthRepository> { AuthRepositoryImpl() }
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { ResetPasswordUseCase(get()) }
    factory { AuthInteractors(login = get(), register = get(), resetPassword = get()) }
    viewModel { 
        AuthViewModel(
            interactors = get(),
            sessionManager = get()
        ) 
    }

    // ── App Settings ────────────────────────────────────────────────────
    single { AppSettingsRepository(androidContext()) }

    // ── feature_dashboard ───────────────────────────────────────────────
    single<DashboardRepository> { DashboardRepositoryImpl(demoStateManager = get()) }
    factory { GetDemoStateUseCase(repository = get()) }
    factory { ToggleDeepWorkUseCase(repository = get()) }
    viewModel { DashboardViewModel(getDemoStateUseCase = get()) }

    // ── feature_employees (Team Velocity + Employee Card) ───────────────
    single<EmployeeRepository> { EmployeeRepositoryImpl() }
    factory { GetEmployeesUseCase(repository = get()) }
    viewModel { EmployeeViewModel(getDemoStateUseCase = get()) }
    viewModel { EmployeeCardViewModel(savedStateHandle = get(), getDemoStateUseCase = get(), toggleDeepWorkUseCase = get()) }

    // ── feature_tasks ───────────────────────────────────────────────────
    factory { ReassignTaskUseCase(repository = get()) }
    viewModel { TaskViewModel(getEmployees = get()) }
    viewModel { TaskReassignmentViewModel(savedStateHandle = get(), getDemoStateUseCase = get(), reassignTaskUseCase = get()) }

    // ── feature_settings ────────────────────────────────────────────────
    viewModel { SettingsViewModel(settingsRepository = get(), sessionManager = get()) }
}
