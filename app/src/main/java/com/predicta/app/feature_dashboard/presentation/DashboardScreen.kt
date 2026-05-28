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
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
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
import com.predicta.app.R
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
        viewModel.effects.collect { action ->
            when (action) {
                DashboardEffect.GoToTeamVelocity -> onNavigateToTeamVelocity()
                is DashboardEffect.ResolveAlert -> onResolveAlert(action.targetId)
            }
        }
    }

    DashboardContent(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = { onEvent(DashboardEvent.Refresh) },
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                val teamHasRisk = state.teamPace.any { it.isRisky }

                SprintStatusCard(
                    sprintName = state.sprintName,
                    hasRisk = teamHasRisk,
                    completionPercent = state.sprintCompletionPercent,
                    elapsedDays = state.sprintElapsedDays,
                    totalDays = state.sprintTotalDays,
                    hasBeenReassigned = state.hasBeenReassigned,
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

            if (state.alerts.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.dashboard_alerts_title),
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

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun SprintStatusCard(
    sprintName: String,
    hasRisk: Boolean,
    completionPercent: Float,
    elapsedDays: Int,
    totalDays: Int,
    hasBeenReassigned: Boolean,
    modifier: Modifier = Modifier,
) {
    val statusColor = if (hasRisk) SemanticCritical else SemanticSuccess
    val statusBgColor = if (hasRisk) BackgroundCritical else BackgroundSuccess
    val textColor = MaterialTheme.colorScheme.primary
    val subTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    var hasAnimatedProgress by rememberSaveable { mutableStateOf(false) }
    val targetProgress = if (hasAnimatedProgress) completionPercent else 0f

    LaunchedEffect(completionPercent) {
        hasAnimatedProgress = true
    }

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
            Text(
                text = if (hasRisk) {
                    stringResource(R.string.dashboard_status_risk)
                } else {
                    stringResource(R.string.dashboard_status_no_risk)
                },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (hasRisk) statusColor else textColor,
            )

            Spacer(modifier = Modifier.height(20.dp))
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AnimatedNumberText(
                        value = (animatedProgress * 100).toInt(),
                        prefix = stringResource(R.string.dashboard_progress_prefix),
                        suffix = stringResource(R.string.dashboard_percent_suffix),
                        style = MaterialTheme.typography.labelMedium,
                        color = subTextColor,
                    )
                    Text(
                        text = stringResource(R.string.dashboard_progress_day, elapsedDays, totalDays),
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
            AnimatedVisibility(
                visible = hasBeenReassigned && !hasRisk,
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
                        text = stringResource(R.string.dashboard_reassignment_synced),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor,
                    )
                }
            }
        }
    }
}

@Composable
private fun SprintVelocityChart(
    teamPace: List<TeamPace>,
    isDelayed: Boolean,
    modifier: Modifier = Modifier,
) {
    var showStoryPointsInfo by remember { mutableStateOf(false) }
    val safeColor = SemanticSuccess
    val riskyColor = if (isDelayed) SemanticWarning else SemanticCritical

    Card(
        shape = PredictaShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.liquidGlass(
            shape = PredictaShapes.medium,
            blurRadius = 0.dp,
            liquidIntensity = 0.8f,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.dashboard_team_title),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )

                Box {
                    IconButton(onClick = { showStoryPointsInfo = !showStoryPointsInfo }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                            contentDescription = stringResource(R.string.dashboard_team_help),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }

                    DropdownMenu(
                        expanded = showStoryPointsInfo,
                        onDismissRequest = { showStoryPointsInfo = false },
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.dashboard_team_help_body),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            },
                            onClick = { showStoryPointsInfo = false },
                        )
                    }
                }
            }

            if (teamPace.isEmpty()) {
                Text(
                    text = stringResource(R.string.dashboard_no_data),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(teamPace) { pace ->
                        val progressBase = maxOf(pace.totalCount, 1)
                        val progress = (pace.completedCount.toFloat() / progressBase.toFloat()).coerceIn(0f, 1f)
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = pace.day,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = stringResource(
                                        R.string.dashboard_task_ratio,
                                        pace.completedCount,
                                        pace.totalCount,
                                    ),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (pace.isRisky) riskyColor else safeColor,
                                )
                            }

                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(999.dp)),
                                color = if (pace.isRisky) riskyColor else safeColor,
                                trackColor = (if (pace.isRisky) riskyColor else safeColor).copy(alpha = 0.14f),
                                strokeCap = StrokeCap.Round,
                            )
                        }
                    }
                }
            }
        }
    }
}

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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                        text = stringResource(R.string.dashboard_resolve),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}


