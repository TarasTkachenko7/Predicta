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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
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
import com.predicta.app.data.demo.DemoData
import com.predicta.app.data.demo.DemoStateManager
import com.predicta.app.data.demo.DemoTask
import com.predicta.app.data.demo.TaskStatus
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
import kotlinx.coroutines.delay

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

    if (state.isPavel) {
        PavelCardContent(
            state = state,
            onBack = onNavigateBack,
            onReassign = onNavigateToReassign,
            onToggleDeepWork = viewModel::onToggleDeepWork,
            onToggleChartMode = viewModel::onToggleChartMode,
            modifier = modifier,
        )
    } else {
        OlegCardContent(
            state = state,
            onBack = onNavigateBack,
            modifier = modifier,
        )
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Pavel's Card — The main demo scenario screen
// ──────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PavelCardContent(
    state: EmployeeCardState,
    onBack: () -> Unit,
    onReassign: (String) -> Unit,
    onToggleDeepWork: () -> Unit,
    onToggleChartMode: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Simulate AI "typing" effect
    var showAiInsight by remember { mutableStateOf(false) }
    var showInsightSheet by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(600)
        showAiInsight = true
    }

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
        // ── Back button + header ────────────────────────────────────────
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Карточка сотрудника",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        // ── Employee header card ────────────────────────────────────────
        item {
            EmployeeHeaderCard(
                name = state.name,
                role = state.role,
                done = state.done,
                total = state.total,
                isHealthy = state.isHealthy,
            )
        }

        // ── Risk Factors ──────────────────────────────────────────────────
        if (state.riskFactors.isNotEmpty()) {
            item {
                Card(
                    shape = PredictaShapes.medium,
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
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
                                text = "Факторы риска",
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
                                    text = factor,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── Forecast chart (plan vs fact) ───────────────────────────────
        item {
            ForecastCard(
                predictedDays = state.predictedDays,
                deadlineDays = state.deadlineDays,
                showRecoveryForecast = state.showRecoveryForecast,
                recoveryForecastData = state.recoveryForecastData,
                onToggleChartMode = onToggleChartMode,
            )
        }

        // ── AI Insight card ─────────────────────────────────────────────
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

        // ── Task list ───────────────────────────────────────────────────
        item {
            Text(
                text = "Текущие задачи",
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

// ──────────────────────────────────────────────────────────────────────────────
// Oleg's Card — simple summary
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun OlegCardContent(
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
                        contentDescription = "Назад",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Карточка сотрудника",
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
                done = state.done,
                total = state.total,
                isHealthy = state.isHealthy,
            )
        }

        item {
            Card(
                shape = PredictaShapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceWhite,
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
                            text = "Оптимальная загрузка",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = SemanticSuccess,
                        )
                        Text(
                            text = if (assignedTasks.isEmpty()) {
                                "Олег закрыл все задачи вовремя и готов принять дополнительную нагрузку."
                            } else {
                                "Олег получил ${assignedTasks.size} новую задачу. Она назначена, но еще не выполнена."
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
                    text = "Новые задачи",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            itemsIndexed(
                items = assignedTasks,
                key = { _, task -> "oleg_${task.id}" },
            ) { _, task ->
                AssignedTaskCard(task = task)
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Shared Components
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun EmployeeHeaderCard(
    name: String,
    role: String,
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
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
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
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(32.dp),
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
                    suffix = " / $total",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = statusColor,
                )
                Text(
                    text = "задач",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun AssignedTaskCard(
    task: DemoTask,
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
                    text = "Назначена Олегу · к выполнению",
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
    showRecoveryForecast: Boolean,
    recoveryForecastData: List<Float>,
    onToggleChartMode: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val planColor = MaterialTheme.colorScheme.primary
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
    val dayLabels = remember { listOf("Сейчас", "+1д", "+2д", "+3д", "+4д", "+5д", "+6д", "+7д", "+8д") }
    val bottomAxisValueFormatter = remember {
        CartesianValueFormatter { _, value, _ ->
            dayLabels.getOrElse(value.toInt()) { "" }
        }
    }

    LaunchedEffect(showRecoveryForecast, recoveryForecastData) {
        modelProducer.runTransaction {
            lineSeries {
                if (showRecoveryForecast) {
                    series(recoveryForecastData)
                } else {
                    // Plan line (ideal pace)
                    series(0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0)
                    // Fact line (Pavel's actual pace — slow)
                    series(0.0, 0.2, 0.3, 0.5, 0.6, 0.7, 0.9, 1.0, 1.1)
                }
            }
        }
    }

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
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = SemanticCritical,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "Прогноз дедлайна",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (showRecoveryForecast) {
                    "При включенной защите фокуса риск выгорания плавно снижается."
                } else {
                    "С текущим темпом Павел закончит свои задачи через $predictedDays дней вместо $deadlineDays."
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (showRecoveryForecast) SemanticSuccess else SemanticCritical,
                fontWeight = FontWeight.Medium,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Legend and Toggle Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (showRecoveryForecast) {
                    LegendItem(color = SemanticSuccess, label = "Прогноз")
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        LegendItem(color = MaterialTheme.colorScheme.primary, label = "План")
                        LegendItem(color = SemanticCritical, label = "Факт")
                    }
                }

                OutlinedButton(
                    onClick = onToggleChartMode,
                    shape = PredictaShapes.medium,
                ) {
                    Text(
                        text = if (showRecoveryForecast) "История нагрузки" else "Прогноз восстановления",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val planLine = LineCartesianLayer.rememberLine(
                fill = remember(planColor) { LineCartesianLayer.LineFill.single(fill(planColor)) },
            )
            val factLine = LineCartesianLayer.rememberLine(
                fill = remember { LineCartesianLayer.LineFill.single(fill(SemanticCritical)) },
            )
            val recoveryLine = LineCartesianLayer.rememberLine(
                fill = remember { LineCartesianLayer.LineFill.single(fill(SemanticSuccess)) },
            )

            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lineProvider = if (showRecoveryForecast) {
                            LineCartesianLayer.LineProvider.series(recoveryLine)
                        } else {
                            LineCartesianLayer.LineProvider.series(planLine, factLine)
                        }
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
                    .height(180.dp),
            )
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
                            Color(0xFFF3E5F5).copy(alpha = 0.3f),
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
                        text = "Аналитика Predicta AI",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = insight,
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
                        text = "Разобрать инсайт",
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
                text = "Как Predicta пришла к выводу",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            InsightReasonRow(
                title = "Темп ниже плана",
                value = "$predictedDays дн.",
                description = "Прогноз завершения превышает дедлайн в $deadlineDays дня.",
            )
            InsightReasonRow(
                title = "Риск перегруза",
                value = "High",
                description = "Открытые задачи сконцентрированы на одном исполнителе.",
            )
            InsightReasonRow(
                title = "Рекомендация",
                value = "Reassign",
                description = "Перенести часть нагрузки на свободного участника команды.",
            )
            Text(
                text = insight,
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
    task: DemoTask,
    onReassign: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val statusColor = when (task.status) {
        TaskStatus.DONE -> SemanticSuccess
        TaskStatus.IN_PROGRESS -> SemanticWarning
        TaskStatus.TODO -> MaterialTheme.colorScheme.onSurfaceVariant
        TaskStatus.REASSIGNED -> MaterialTheme.colorScheme.primary
    }

    val statusLabel = when (task.status) {
        TaskStatus.DONE -> "Выполнено"
        TaskStatus.IN_PROGRESS -> "В работе"
        TaskStatus.TODO -> "Ожидает"
        TaskStatus.REASSIGNED -> "Переназначена → ${task.assigneeName}"
    }

    val canReassign = task.status == TaskStatus.IN_PROGRESS || task.status == TaskStatus.TODO

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
                // Status dot
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
                        color = if (task.status == TaskStatus.REASSIGNED) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else MaterialTheme.colorScheme.primary,
                        textDecoration = if (task.status == TaskStatus.REASSIGNED) {
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
                        text = "Перераспределить",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}
