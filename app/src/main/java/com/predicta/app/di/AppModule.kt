package com.predicta.app.di

import com.predicta.app.data.demo.DemoStateManager
import com.predicta.app.core.network.NetworkMonitor
import com.predicta.app.feature_auth.data.session.UserSessionManager
import com.predicta.app.feature_dashboard.presentation.DashboardViewModel
import com.predicta.app.feature_employees.presentation.EmployeeViewModel
import com.predicta.app.feature_employees.presentation.EmployeeCardViewModel
import com.predicta.app.feature_auth.data.repository.AuthRepositoryImpl
import com.predicta.app.feature_auth.domain.repository.AuthRepository
import com.predicta.app.feature_auth.presentation.AuthViewModel
import com.predicta.app.feature_settings.data.repository.AppSettingsRepository
import com.predicta.app.feature_settings.presentation.SettingsViewModel
import com.predicta.app.feature_tasks.presentation.TaskReassignmentViewModel
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
    viewModel { com.predicta.app.ui.AppViewModel(networkMonitor = get(), sessionManager = get()) }

    // ── Auth Feature ────────────────────────────────────────────────────
    single<AuthRepository> { AuthRepositoryImpl() }
    factory { com.predicta.app.feature_auth.domain.usecase.LoginUseCase(get()) }
    factory { com.predicta.app.feature_auth.domain.usecase.RegisterUseCase(get()) }
    factory { com.predicta.app.feature_auth.domain.usecase.ResetPasswordUseCase(get()) }
    viewModel { 
        AuthViewModel(
            loginUseCase = get(),
            registerUseCase = get(),
            resetPasswordUseCase = get(),
            sessionManager = get()
        ) 
    }

    // ── App Settings ────────────────────────────────────────────────────
    single { AppSettingsRepository(androidContext()) }

    // ── feature_dashboard ───────────────────────────────────────────────
    factory { com.predicta.app.feature_dashboard.domain.usecase.GetDemoStateUseCase(get()) }
    factory { com.predicta.app.feature_employees.domain.usecase.ToggleDeepWorkUseCase(demoStateManager = get()) }
    viewModel { DashboardViewModel(getDemoStateUseCase = get()) }

    // ── feature_employees (Team Velocity + Employee Card) ───────────────
    viewModel { EmployeeViewModel(getDemoStateUseCase = get()) }
    viewModel { EmployeeCardViewModel(savedStateHandle = get(), getDemoStateUseCase = get(), toggleDeepWorkUseCase = get()) }

    // ── feature_tasks ───────────────────────────────────────────────────
    factory { com.predicta.app.feature_tasks.domain.usecase.ReassignTaskUseCase(get()) }
    viewModel { TaskReassignmentViewModel(savedStateHandle = get(), getDemoStateUseCase = get(), reassignTaskUseCase = get()) }

    // ── feature_settings ────────────────────────────────────────────────
    viewModel { SettingsViewModel(settingsRepository = get(), sessionManager = get()) }
}
