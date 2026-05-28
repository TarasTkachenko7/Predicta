package com.predicta.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.annotation.StringRes
import com.predicta.app.R

sealed class Screen(
    val route: String,
    @StringRes val labelRes: Int = 0,
    val selectedIcon: ImageVector = Icons.Default.Dashboard,
    val unselectedIcon: ImageVector = Icons.Outlined.Dashboard,
) {
    data object StartupVideo : Screen("startup_video")
    data object Login : Screen("login")

    data object Dashboard : Screen(
        route = "dashboard",
        labelRes = R.string.nav_project,
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard,
    )

    data object TeamVelocity : Screen(
        route = "team_velocity",
        labelRes = R.string.nav_team,
        selectedIcon = Icons.Filled.People,
        unselectedIcon = Icons.Outlined.People,
    )

    data object Settings : Screen(
        route = "settings",
        labelRes = R.string.nav_settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
    )

    data object EmployeeCard : Screen(
        route = "employee_card/{employeeId}",
    ) {
        fun createRoute(employeeId: String): String = "employee_card/$employeeId"
    }

    data object TaskReassignment : Screen(
        route = "task_reassignment/{taskId}",
    ) {
        fun createRoute(taskId: String): String = "task_reassignment/$taskId"
    }

    companion object {
        val bottomNavItems = listOf(Dashboard, TeamVelocity, Settings)
    }
}
