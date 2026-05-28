package com.predicta.app.feature_employees.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.predicta.app.R
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
import com.predicta.app.core.ui.formatBackendText
import com.predicta.app.feature_dashboard.domain.model.DashboardTask
import com.predicta.app.feature_dashboard.domain.model.DashboardTaskStatus
import com.predicta.app.ui.components.AnimatedNumberText
import com.predicta.app.ui.modifier.liquidGlass
import com.predicta.app.ui.modifier.pressScale
import com.predicta.app.ui.theme.BackgroundCritical
import com.predicta.app.ui.theme.BackgroundSuccess
import com.predicta.app.ui.theme.BackgroundWarning
import com.predicta.app.ui.theme.BurnoutLevel
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
fun EmployeeCardScreen(
    employeeId: String,
    onNavigateBack: () -> Unit,
    onNavigateToReassign: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EmployeeCardViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) return

    if (state.showAnalytics) {
        EmployeeAnalyticsContent(
            state = state,
            onBack = onNavigateBack,
            onReassign = onNavigateToReassign,
            onToggleDeepWork = viewModel::onToggleDeepWork,
            modifier = modifier,
        )
    } else {
        EmployeeSummaryContent(
            state = state,
            onBack = onNavigateBack,
            modifier = modifier,
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmployeeAnalyticsContent(
    state: EmployeeCardState,
    onBack: () -> Unit,
    onReassign: (String) -> Unit,
    onToggleDeepWork: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAiInsight by remember { mutableStateOf(true) }
    var showInsightSheet by remember { mutableStateOf(false) }

    if (showInsightSheet) {
        AiInsightBottomSheet(
            insight = state.aiInsight,
            predictedDays = state.predictedDays,
            deadlineDays = state.deadlineDays,
            onDismiss = { showInsightSheet = false },
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.common_back),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.employee_card_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        item {
            EmployeeHeaderCard(
                name = state.name,
                role = state.role,
                avatarUrl = state.avatarUrl,
                done = state.done,
                total = state.total,
                isHealthy = state.isHealthy,
            )
        }
        if (state.riskFactors.isNotEmpty()) {
            item {
                Card(
                    shape = PredictaShapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(
                            shape = PredictaShapes.medium,
                            blurRadius = 0.dp,
                            tintColor = SemanticWarning,
                            tintAlpha = 0.08f,
                            isActive = true,
                        )
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
                                imageVector = Icons.Outlined.Warning,
                                contentDescription = null,
                                tint = SemanticWarning,
                                modifier = Modifier.size(20.dp),
                            )
                            Text(
                                text = stringResource(R.string.employee_card_risk_factors),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        state.riskFactors.forEach { factor ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(bottom = 6.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(SemanticWarning),
                                )
                                Text(
                    text = factor.formatBackendText(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
        item {
            ForecastCard(
                predictedDays = state.predictedDays,
                deadlineDays = state.deadlineDays,
            )
        }
        item {
            AnimatedVisibility(
                visible = showAiInsight,
                enter = fadeIn(tween(800)) + slideInVertically(tween(800)),
            ) {
                AiInsightCard(
                    insight = state.aiInsight,
                    onOpenDetails = { showInsightSheet = true },
                )
            }
        }
        item {
            Text(
                text = stringResource(R.string.employee_card_current_tasks),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        itemsIndexed(
            items = state.tasks,
            key = { _, task -> task.id },
        ) { _, task ->
            TaskCard(
                task = task,
                onReassign = { onReassign(task.id) },
            )
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}
@Composable
private fun EmployeeSummaryContent(
    state: EmployeeCardState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val assignedTasks = state.tasks

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.common_back),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.employee_card_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        item {
            EmployeeHeaderCard(
                name = state.name,
                role = state.role,
                avatarUrl = state.avatarUrl,
                done = state.done,
                total = state.total,
                isHealthy = state.isHealthy,
            )
        }

        item {
            Card(
                shape = PredictaShapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        shape = PredictaShapes.medium,
                        blurRadius = 0.dp,
                        tintColor = SemanticSuccess,
                        tintAlpha = 0.08f,
                    ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = SemanticSuccess,
                        modifier = Modifier.size(28.dp),
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.employee_card_optimal_load),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = SemanticSuccess,
                        )
                        Text(
                            text = if (assignedTasks.isEmpty()) {
                                stringResource(R.string.employee_card_ready_text)
                            } else {
                                stringResource(R.string.employee_card_assigned_text, assignedTasks.size)
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }
        }

        if (assignedTasks.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.employee_card_new_tasks),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            itemsIndexed(
                items = assignedTasks,
                key = { _, task -> "assigned_${task.id}" },
            ) { _, task ->
                AssignedTaskCard(task = task)
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun EmployeeHeaderCard(
    name: String,
    role: String,
    avatarUrl: String?,
    done: Int,
    total: Int,
    isHealthy: Boolean,
    modifier: Modifier = Modifier,
) {
    val burnoutLevel = if (isHealthy) BurnoutLevel.LOW else BurnoutLevel.HIGH
    val statusColor = burnoutLevel.getStrokeColor()
    val cardBgColor = burnoutLevel.getBackgroundColor()

    Card(
        shape = PredictaShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = PredictaShapes.medium,
                blurRadius = 0.dp,
                tintColor = statusColor,
                tintAlpha = 0.08f,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                EmployeeAvatar(
                    avatarUrl = avatarUrl,
                    tint = statusColor,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = role,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                AnimatedNumberText(
                    value = done,
                    suffix = stringResource(R.string.employee_card_count_suffix, total),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = statusColor,
                )
                Text(
                    text = stringResource(R.string.employee_card_tasks_suffix),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun EmployeeAvatar(
    avatarUrl: String?,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    if (avatarUrl.isNullOrBlank()) {
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = null,
            tint = tint,
            modifier = modifier.size(32.dp),
        )
        return
    }

    val fallbackPainter = rememberVectorPainter(Icons.Outlined.Person)

    AsyncImage(
        model = avatarUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxSize()
            .clip(CircleShape),
        placeholder = fallbackPainter,
        error = fallbackPainter,
        fallback = fallbackPainter,
    )
}

@Composable
private fun AssignedTaskCard(
    task: DashboardTask,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = PredictaShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = PredictaShapes.medium,
                blurRadius = 0.dp,
                tintColor = SemanticWarning,
                tintAlpha = 0.08f,
                isActive = true,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(SemanticWarning),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(R.string.employee_card_assigned_to),
                    style = MaterialTheme.typography.labelSmall,
                    color = SemanticWarning,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
    }
}

@Composable
private fun ForecastCard(
    predictedDays: Int,
    deadlineDays: Int,
    modifier: Modifier = Modifier,
) {
    val progressBase = maxOf(predictedDays, deadlineDays, 1)
    val predictedProgress = predictedDays.toFloat() / progressBase.toFloat()
    val deadlineProgress = deadlineDays.toFloat() / progressBase.toFloat()
    val statusColor = if (predictedDays <= deadlineDays) SemanticSuccess else SemanticCritical
    val deltaDays = kotlin.math.abs(deadlineDays - predictedDays)

    Card(
        shape = PredictaShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = PredictaShapes.medium,
                blurRadius = 0.dp,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.employee_card_deadline_forecast),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = if (predictedDays <= deadlineDays) {
                    stringResource(R.string.employee_card_forecast_on_time, deltaDays)
                } else {
                    stringResource(R.string.employee_card_forecast_risk, deltaDays)
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = statusColor,
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ForecastBarRow(
                    label = stringResource(R.string.employee_card_forecast_plan),
                    value = predictedDays,
                    progress = predictedProgress.coerceIn(0f, 1f),
                    barColor = statusColor,
                )
                ForecastBarRow(
                    label = stringResource(R.string.employee_card_forecast_left),
                    value = deadlineDays,
                    progress = deadlineProgress.coerceIn(0f, 1f),
                    barColor = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun ForecastBarRow(
    label: String,
    value: Int,
    progress: Float,
    barColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.employee_card_days_suffix, value),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(999.dp)),
            color = barColor,
            trackColor = barColor.copy(alpha = 0.15f),
            strokeCap = StrokeCap.Round,
        )
    }
}

@Composable
private fun AiInsightCard(
    insight: String,
    onOpenDetails: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = PredictaShapes.medium,
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = PredictaShapes.medium,
                blurRadius = 0.dp,
                isActive = true,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                        ),
                    ),
                )
                .padding(20.dp),
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp),
                    )
                    Text(
                        text = stringResource(R.string.employee_card_ai_title),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = insight.formatBackendText(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = onOpenDetails,
                    shape = PredictaShapes.medium,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(R.string.employee_card_ai_button),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AiInsightBottomSheet(
    insight: String,
    predictedDays: Int,
    deadlineDays: Int,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = stringResource(R.string.employee_card_ai_explainer_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            InsightReasonRow(
                title = stringResource(R.string.employee_card_ai_reason_slow),
                value = stringResource(R.string.employee_card_days_short, predictedDays),
                description = stringResource(R.string.employee_card_ai_reason_slow_description, deadlineDays),
            )
            InsightReasonRow(
                title = stringResource(R.string.employee_card_ai_reason_risk),
                value = stringResource(R.string.employee_card_high),
                description = stringResource(R.string.employee_card_ai_reason_risk_description),
            )
            InsightReasonRow(
                title = stringResource(R.string.employee_card_ai_recommendation),
                value = stringResource(R.string.employee_card_reassign_value),
                description = stringResource(R.string.employee_card_ai_recommendation_description),
            )
            Text(
                text = insight.formatBackendText(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun InsightReasonRow(
    title: String,
    value: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = PredictaShapes.medium,
                blurRadius = 0.dp,
                isActive = true,
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun TaskCard(
    task: DashboardTask,
    onReassign: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val statusColor = when (task.status) {
        DashboardTaskStatus.DONE -> SemanticSuccess
        DashboardTaskStatus.IN_PROGRESS -> SemanticWarning
        DashboardTaskStatus.TODO -> MaterialTheme.colorScheme.onSurfaceVariant
        DashboardTaskStatus.REASSIGNED -> MaterialTheme.colorScheme.primary
    }

    val statusLabel = when (task.status) {
        DashboardTaskStatus.DONE -> "Выполнено"
        DashboardTaskStatus.IN_PROGRESS -> "В работе"
        DashboardTaskStatus.TODO -> "Ожидает"
        DashboardTaskStatus.REASSIGNED -> "Переназначена → ${task.assigneeName}"
    }

    val canReassign = task.status == DashboardTaskStatus.IN_PROGRESS ||
        task.status == DashboardTaskStatus.TODO

    Card(
        shape = PredictaShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .liquidGlass(
                shape = PredictaShapes.medium,
                blurRadius = 0.dp,
                isActive = canReassign,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(statusColor),
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (task.status == DashboardTaskStatus.REASSIGNED) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else MaterialTheme.colorScheme.primary,
                        textDecoration = if (task.status == DashboardTaskStatus.REASSIGNED) {
                            TextDecoration.LineThrough
                        } else TextDecoration.None,
                    )
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }

            if (canReassign) {
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onReassign,
                    modifier = Modifier.fillMaxWidth(),
                    shape = PredictaShapes.medium,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Filled.SwapHoriz,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.employee_card_reassign),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

