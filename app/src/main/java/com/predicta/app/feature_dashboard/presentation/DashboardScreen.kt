package com.predicta.app.feature_dashboard.presentation

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.predicta.app.feature_dashboard.domain.model.GlobalAlert
import com.predicta.app.feature_dashboard.domain.model.TeamPace
import com.predicta.app.ui.theme.ErrorRed
import com.predicta.app.ui.theme.PredictaShapes
import com.predicta.app.ui.theme.PrimaryBlue
import com.predicta.app.ui.theme.SecondarySlate
import com.predicta.app.ui.theme.SurfaceWhite
import com.predicta.app.ui.theme.TextSecondary
import com.predicta.app.ui.theme.WarningAmber
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    DashboardContent(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

@Composable
private fun DashboardContent(
    state: DashboardState,
    onEvent: (DashboardEvent) -> Unit,
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
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // ── Section: Sprint Velocity Chart ──────────────────────────────
        item {
            Text(
                text = "Sprint Velocity",
                style = MaterialTheme.typography.titleMedium,
                color = PrimaryBlue,
                fontWeight = FontWeight.SemiBold,
            )
        }

        item {
            SprintVelocityChart(
                teamPace = state.teamPace,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
            )
        }

        // ── Section: AI Alerts ──────────────────────────────────────────
        if (state.alerts.isNotEmpty()) {
            item {
                Text(
                    text = "AI Alerts",
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            items(
                items = state.alerts,
                key = { it.id },
            ) { alert ->
                AlertCard(
                    alert = alert,
                    onDismiss = { onEvent(DashboardEvent.DismissAlert(alert.id)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                )
            }
        }

        // Bottom spacer for comfortable scrolling above the nav bar
        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Vico Chart: Sprint Velocity (Line)
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun SprintVelocityChart(
    teamPace: List<TeamPace>,
    modifier: Modifier = Modifier,
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val dayLabels = remember(teamPace) { teamPace.map { it.day } }
    val bottomAxisValueFormatter = remember(dayLabels) {
        CartesianValueFormatter { _, value, _ ->
            dayLabels.getOrElse(value.toInt()) { "" }
        }
    }

    LaunchedEffect(teamPace) {
        modelProducer.runTransaction {
            lineSeries {
                series(teamPace.map { it.velocity.toDouble() })
            }
        }
    }

    Card(
        shape = PredictaShapes.medium,
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        modifier = modifier.border(
            width = 0.5.dp,
            color = SecondarySlate.copy(alpha = 0.15f),
            shape = PredictaShapes.medium,
        ),
    ) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    lineProvider = LineCartesianLayer.LineProvider.series(
                        LineCartesianLayer.rememberLine(fill = remember { LineCartesianLayer.LineFill.single(fill(PrimaryBlue)) }),
                    ),
                ),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(valueFormatter = bottomAxisValueFormatter),
            ),
            modelProducer = modelProducer,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Alert Card
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun AlertCard(
    alert: GlobalAlert,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val severityColor = when (alert.severity) {
        "high" -> ErrorRed
        "medium" -> WarningAmber
        else -> SecondarySlate
    }

    Card(
        shape = PredictaShapes.medium,
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        modifier = modifier.border(
            width = 0.5.dp,
            color = SecondarySlate.copy(alpha = 0.15f),
            shape = PredictaShapes.medium,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            // Severity indicator dot
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(severityColor),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.severity.replaceFirstChar { it.uppercase() } + " Priority",
                    style = MaterialTheme.typography.labelMedium,
                    color = severityColor,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = alert.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Dismiss alert",
                    tint = SecondarySlate.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}
