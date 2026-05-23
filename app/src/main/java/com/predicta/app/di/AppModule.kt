package com.predicta.app.di

import com.predicta.app.data.demo.DemoStateManager
import com.predicta.app.feature_dashboard.presentation.DashboardViewModel
import com.predicta.app.feature_employees.presentation.EmployeeViewModel
import com.predicta.app.settings.AppSettingsRepository
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

    // ── App Settings ────────────────────────────────────────────────────
    single { AppSettingsRepository(androidContext()) }

    // ── feature_dashboard ───────────────────────────────────────────────
    viewModel { DashboardViewModel(demoStateManager = get()) }

    // ── feature_employees (Team Velocity + Employee Card) ───────────────
    viewModel { EmployeeViewModel(demoStateManager = get()) }
}
