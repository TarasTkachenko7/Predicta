package com.predicta.app.feature_dashboard.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisTickComponent
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
import com.predicta.app.ui.components.AnimatedNumberText
import com.predicta.app.ui.modifier.liquidGlass
import com.predicta.app.ui.theme.BackgroundCritical
import com.predicta.app.ui.theme.BackgroundSuccess
import com.predicta.app.ui.theme.BackgroundWarning
import com.predicta.app.ui.theme.PredictaShapes
import com.predicta.app.ui.theme.PrimaryBlue
import com.predicta.app.ui.theme.SecondarySlate
import com.predicta.app.ui.theme.SemanticCritical
import com.predicta.app.ui.theme.SemanticSuccess
import com.predicta.app.ui.theme.SemanticWarning
import com.predicta.app.ui.theme.SurfaceWhite
import com.predicta.app.ui.theme.TextSecondary
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreen(
    onNavigateToTeamVelocity: () -> Unit,
    onResolveAlert: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigation.collect { action ->
            when (action) {
                DashboardNavAction.GoToTeamVelocity -> onNavigateToTeamVelocity()
                is DashboardNavAction.ResolveAlert -> onResolveAlert(action.targetId)
            }
        }
    }

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
                color = MaterialTheme.colorScheme.primary,
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
        // ── Section: Sprint Status Widget ────────────────────────────────
        item {
            SprintStatusCard(
                sprintName = state.sprintName,
                isDelayed = state.isProjectDelayed,
                delayDays = state.delayDays,
                delayTrack = state.delayTrack,
                completionPercent = state.sprintCompletionPercent,
                elapsedDays = state.sprintElapsedDays,
                totalDays = state.sprintTotalDays,
                hasBeenReassigned = state.hasBeenReassigned,
            )
        }

        // ── Section: Sprint Velocity Chart ──────────────────────────────
        item {
            Text(
                text = "Темп команды (Story Points / день)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )
        }

        item {
            SprintVelocityChart(
                teamPace = state.teamPace,
                isDelayed = state.isProjectDelayed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
            )
        }

        // ── Section: AI Alerts ──────────────────────────────────────────
        if (state.alerts.isNotEmpty()) {
            item {
                Text(
                    text = "Предупреждения",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
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
                    onResolve = { onEvent(DashboardEvent.AlertClicked(alert.id)) },
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
// Sprint Status Card — the hero widget
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun SprintStatusCard(
    sprintName: String,
    isDelayed: Boolean,
    delayDays: Int,
    delayTrack: String,
    completionPercent: Float,
    elapsedDays: Int,
    totalDays: Int,
    hasBeenReassigned: Boolean,
    modifier: Modifier = Modifier,
) {
    val statusColor = if (isDelayed) SemanticCritical else SemanticSuccess
    val statusBgColor = if (isDelayed) BackgroundCritical else BackgroundSuccess
    val textColor = MaterialTheme.colorScheme.primary
    val subTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    // Animated completion progress
    var targetProgress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(completionPercent) { targetProgress = completionPercent }
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 1000),
        label = "sprint_progress",
    )

    Card(
        shape = PredictaShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = PredictaShapes.medium,
                blurRadius = 0.dp,
                liquidIntensity = 0.9f,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
        ) {
            // Sprint label
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = sprintName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status message
            Text(
                text = if (isDelayed) {
                    "Риск срыва дедлайна $delayTrack на $delayDays дня"
                } else {
                    "Новый прогноз проекта: Сдача вовремя"
                },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (isDelayed) statusColor else textColor,
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Progress bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AnimatedNumberText(
                        value = (animatedProgress * 100).toInt(),
                        prefix = "Выполнено: ",
                        suffix = "%",
                        style = MaterialTheme.typography.labelMedium,
                        color = subTextColor,
                    )
                    Text(
                        text = "День $elapsedDays из $totalDays",
                        style = MaterialTheme.typography.labelMedium,
                        color = subTextColor,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = statusColor,
                    trackColor = statusColor.copy(alpha = 0.15f),
                    strokeCap = StrokeCap.Round,
                )
            }

            // Reassignment success badge
            AnimatedVisibility(
                visible = hasBeenReassigned && !isDelayed,
                enter = fadeIn(tween(600)) + slideInVertically(tween(600)),
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = "Сроки в Jira обновлены. Проект выровнен.",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor,
                    )
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Vico Chart: Sprint Velocity (Line)
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun SprintVelocityChart(
    teamPace: List<TeamPace>,
    isDelayed: Boolean,
    modifier: Modifier = Modifier,
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val lineColor = MaterialTheme.colorScheme.primary
    val axisLabel = rememberAxisLabelComponent(
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    val axisLine = rememberAxisLineComponent(
        fill = fill(MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)),
    )
    val axisTick = rememberAxisTickComponent(
        fill = fill(MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
    )
    val axisGuideline = rememberAxisGuidelineComponent(
        fill = fill(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.38f)),
    )
    val dayLabels = remember(teamPace) { teamPace.map { it.day } }
    val bottomAxisValueFormatter = remember(dayLabels) {
        CartesianValueFormatter { _, value, _ ->
            dayLabels.getOrElse(value.toInt()) { "" }
        }
    }

    LaunchedEffect(teamPace) {
        if (teamPace.isNotEmpty()) {
            modelProducer.runTransaction {
                lineSeries {
                    series(teamPace.map { it.velocity.toDouble() })
                }
            }
        }
    }

    Card(
        shape = PredictaShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.liquidGlass(
            shape = PredictaShapes.medium,
            blurRadius = 0.dp,
            liquidIntensity = 0.8f,
        ),
    ) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    lineProvider = LineCartesianLayer.LineProvider.series(
                        LineCartesianLayer.rememberLine(
                            fill = remember(lineColor) {
                                LineCartesianLayer.LineFill.single(fill(lineColor))
                            },
                        ),
                    ),
                ),
                startAxis = VerticalAxis.rememberStart(
                    line = axisLine,
                    label = axisLabel,
                    tick = axisTick,
                    guideline = axisGuideline,
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    line = axisLine,
                    label = axisLabel,
                    tick = axisTick,
                    guideline = axisGuideline,
                    valueFormatter = bottomAxisValueFormatter,
                ),
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
    onResolve: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val severityColor = when (alert.severity) {
        "high" -> SemanticCritical
        "medium" -> SemanticWarning
        "success" -> SemanticSuccess
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        shape = PredictaShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = PredictaShapes.medium,
                blurRadius = 0.dp,
                liquidIntensity = 0.9f,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Severity indicator dot
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(severityColor),
                    )
                    Text(
                        text = when (alert.severity) {
                            "high" -> "Критический"
                            "medium" -> "Предупреждение"
                            "success" -> "Успех"
                            else -> "Инфо"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        color = severityColor,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Text(
                    text = alert.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
                )
                
                Button(
                    onClick = onResolve,
                    shape = PredictaShapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = SurfaceWhite,
                    ),
                ) {
                    Text(
                        text = "Решить",
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

