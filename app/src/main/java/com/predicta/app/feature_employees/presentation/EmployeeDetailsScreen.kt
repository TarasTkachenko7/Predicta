package com.predicta.app.feature_employees.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.predicta.app.feature_employees.domain.model.Employee
import com.predicta.app.ui.theme.ErrorRed
import com.predicta.app.ui.theme.PredictaShapes
import com.predicta.app.ui.theme.PrimaryBlue
import com.predicta.app.ui.theme.SecondarySlate
import com.predicta.app.ui.theme.SuccessGreen
import com.predicta.app.ui.theme.SurfaceWhite
import com.predicta.app.ui.theme.TextSecondary
import com.predicta.app.ui.theme.WarningAmber
import org.koin.androidx.compose.koinViewModel

@Composable
fun EmployeeDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: EmployeeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    EmployeeContent(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

@Composable
private fun EmployeeContent(
    state: EmployeeState,
    onEvent: (EmployeeEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                color = PrimaryBlue,
                strokeWidth = 3.dp,
                modifier = Modifier.size(40.dp),
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = "Team Members",
                style = MaterialTheme.typography.titleMedium,
                color = PrimaryBlue,
                fontWeight = FontWeight.SemiBold,
            )
        }

        items(
            items = state.employees,
            key = { it.id },
        ) { employee ->
            EmployeeCard(employee = employee)
        }

        // ── Historical Performance section ──────────────────────────────
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Historical Performance",
                style = MaterialTheme.typography.titleMedium,
                color = PrimaryBlue,
                fontWeight = FontWeight.SemiBold,
            )
        }

        item {
            HistoricalPerformanceCard()
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Employee Card with Circular Progress Indicators
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun EmployeeCard(
    employee: Employee,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = PredictaShapes.medium,
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 0.5.dp,
                color = SecondarySlate.copy(alpha = 0.15f),
                shape = PredictaShapes.medium,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            // Name & role
            Text(
                text = employee.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryBlue,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = employee.role,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp),
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Metric gauges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                MetricGauge(
                    label = "Workload",
                    value = employee.workloadPercentage,
                    color = when {
                        employee.workloadPercentage > 0.85f -> ErrorRed
                        employee.workloadPercentage > 0.65f -> WarningAmber
                        else -> PrimaryBlue
                    },
                )
                MetricGauge(
                    label = "Burnout Risk",
                    value = employee.burnoutRisk,
                    color = when {
                        employee.burnoutRisk > 0.7f -> ErrorRed
                        employee.burnoutRisk > 0.45f -> WarningAmber
                        else -> SuccessGreen
                    },
                )
            }
        }
    }
}

/**
 * Animated circular progress gauge with percentage label.
 */
@Composable
private fun MetricGauge(
    label: String,
    value: Float,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    // Animate from 0 to actual value on first composition
    var targetValue by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(value) { targetValue = value }

    val animatedValue by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis = 800),
        label = "gauge_$label",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Background track
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.size(72.dp),
                color = color.copy(alpha = 0.12f),
                strokeWidth = 6.dp,
                strokeCap = StrokeCap.Round,
            )
            // Foreground progress
            CircularProgressIndicator(
                progress = { animatedValue },
                modifier = Modifier.size(72.dp),
                color = color,
                strokeWidth = 6.dp,
                strokeCap = StrokeCap.Round,
            )
            // Percentage text
            Text(
                text = "${(animatedValue * 100).toInt()}%",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = color,
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Historical Performance Card
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun HistoricalPerformanceCard(modifier: Modifier = Modifier) {
    Card(
        shape = PredictaShapes.medium,
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 0.5.dp,
                color = SecondarySlate.copy(alpha = 0.15f),
                shape = PredictaShapes.medium,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.TrendingUp,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "Sprint Metrics (Last 4 Sprints)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlue,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Metric rows
            MetricRow(label = "Avg. Story Points", value = "34.5 pts/sprint")
            MetricRow(label = "On-Time Delivery", value = "88%")
            MetricRow(label = "Bug Escape Rate", value = "4.2%")
            MetricRow(label = "Team Satisfaction", value = "7.1 / 10")
        }
    }
}

@Composable
private fun MetricRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryBlue,
        )
    }
}
