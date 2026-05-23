package com.predicta.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.predicta.app.R
import com.predicta.app.data.demo.DemoStateManager
import com.predicta.app.feature_dashboard.presentation.DashboardScreen
import com.predicta.app.feature_employees.presentation.EmployeeCardScreen
import com.predicta.app.feature_employees.presentation.TeamVelocityScreen
import com.predicta.app.feature_tasks.presentation.TaskReassignmentScreen
import com.predicta.app.navigation.Screen
import com.predicta.app.ui.theme.PrimaryBlue
import com.predicta.app.ui.theme.SecondarySlate
import com.predicta.app.ui.theme.SuccessGreen
import com.predicta.app.ui.theme.SurfaceWhite
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictaScaffold(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val demoStateManager: DemoStateManager = koinInject()

    // Only show bottom bar on top-level screens
    val showBottomBar = currentRoute in Screen.bottomNavItems.map { it.route }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            PredictaTopBar()
        },
        bottomBar = {
            if (showBottomBar) {
                PredictaBottomBar(
                    currentRoute = currentRoute,
                    onItemSelected = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            // Экран 1: Дашборд (Здоровье проекта)
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToTeamVelocity = {
                        navController.navigate(Screen.TeamVelocity.route) {
                            launchSingleTop = true
                        }
                    },
                )
            }

            // Экран 2: Анализ темпа работы (Команда)
            composable(Screen.TeamVelocity.route) {
                TeamVelocityScreen(
                    onNavigateToEmployeeCard = { employeeId ->
                        navController.navigate(
                            Screen.EmployeeCard.createRoute(employeeId),
                        )
                    },
                )
            }

            // Экран 3: Карточка сотрудника & AI-инсайты
            composable(
                route = Screen.EmployeeCard.route,
                arguments = listOf(
                    navArgument("employeeId") { type = NavType.StringType },
                ),
            ) { backStackEntry ->
                val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
                EmployeeCardScreen(
                    employeeId = employeeId,
                    demoStateManager = demoStateManager,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToReassign = { taskId ->
                        navController.navigate(
                            Screen.TaskReassignment.createRoute(taskId),
                        )
                    },
                )
            }

            // Экран 4: Перераспределение задачи
            composable(
                route = Screen.TaskReassignment.route,
                arguments = listOf(
                    navArgument("taskId") { type = NavType.StringType },
                ),
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                TaskReassignmentScreen(
                    taskId = taskId,
                    demoStateManager = demoStateManager,
                    onNavigateBack = { navController.popBackStack() },
                    onReassignmentComplete = {
                        // Navigate back to Dashboard, clearing the stack
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Top App Bar
// ──────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PredictaTopBar() {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Predicta",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp)),
                )
                Text(
                    text = "Predicta",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp,
                    ),
                    color = PrimaryBlue,
                )
                AiStatusIndicator()
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = SurfaceWhite,
            scrolledContainerColor = SurfaceWhite,
        ),
        modifier = Modifier.shadow(
            elevation = 2.dp,
            ambientColor = Color.Black.copy(alpha = 0.06f),
            spotColor = Color.Black.copy(alpha = 0.06f),
        ),
    )
}

/**
 * A small pulsing green dot indicating that the AI assistant is active.
 */
@Composable
private fun AiStatusIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "ai_pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "ai_pulse_alpha",
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .alpha(alpha)
                .clip(CircleShape)
                .background(SuccessGreen),
        )
        Text(
            text = "AI Active",
            style = MaterialTheme.typography.labelSmall,
            color = SecondarySlate.copy(alpha = 0.7f),
        )
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Bottom Navigation Bar
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun PredictaBottomBar(
    currentRoute: String?,
    onItemSelected: (Screen) -> Unit,
) {
    NavigationBar(
        containerColor = SurfaceWhite,
        tonalElevation = 0.dp,
        modifier = Modifier.shadow(
            elevation = 8.dp,
            ambientColor = Color.Black.copy(alpha = 0.04f),
            spotColor = Color.Black.copy(alpha = 0.04f),
        ),
    ) {
        Screen.bottomNavItems.forEach { screen ->
            val isSelected = currentRoute == screen.route

            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(screen) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                        contentDescription = screen.label,
                    )
                },
                label = {
                    Text(
                        text = screen.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    unselectedIconColor = SecondarySlate,
                    unselectedTextColor = SecondarySlate,
                    indicatorColor = PrimaryBlue.copy(alpha = 0.08f),
                ),
            )
        }
    }
}
