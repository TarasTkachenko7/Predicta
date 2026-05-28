package com.predicta.app.feature_employees.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.predicta.app.feature_employees.domain.model.Employee
import com.predicta.app.ui.components.AnimatedNumberText
import com.predicta.app.ui.modifier.liquidGlass
import com.predicta.app.ui.modifier.pressScale
import com.predicta.app.ui.theme.BackgroundCritical
import com.predicta.app.ui.theme.BackgroundSuccess
import com.predicta.app.ui.theme.BurnoutLevel
import com.predicta.app.ui.theme.PredictaShapes
import com.predicta.app.ui.theme.PrimaryBlue
import com.predicta.app.ui.theme.SecondarySlate
import com.predicta.app.ui.theme.SemanticCritical
import com.predicta.app.ui.theme.SemanticSuccess
import com.predicta.app.ui.theme.SurfaceWhite
import com.predicta.app.ui.theme.TextSecondary
import org.koin.androidx.compose.koinViewModel

@Composable
fun TeamVelocityScreen(
    onNavigateToEmployeeCard: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EmployeeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { action ->
            when (action) {
                is EmployeeEffect.GoToEmployeeCard -> {
                    onNavigateToEmployeeCard(action.employeeId)
                }
            }
        }
    }

    TeamVelocityContent(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

@Composable
private fun TeamVelocityContent(
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
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = "Анализ темпа работы",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Данные из Predicta API",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        items(
            items = state.employees,
            key = { it.id },
        ) { employee ->
            VelocityCard(
                name = employee.name,
                role = employee.role,
                done = employee.doneCount,
                total = employee.totalCount,
                isHealthy = employee.burnoutRisk < 0.7f,
                avatarUrl = employee.avatarUrl,
                onClick = { onEvent(EmployeeEvent.SelectEmployee(employee.id)) },
            )
        }

        item {
            SummaryCard(employees = state.employees)
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Velocity Card — horizontal progress bar per employee
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun VelocityCard(
    name: String,
    role: String,
    done: Int,
    total: Int,
    isHealthy: Boolean,
    avatarUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val progress = if (total > 0) done.toFloat() / total else 0f
    val burnoutLevel = if (isHealthy) BurnoutLevel.LOW else BurnoutLevel.HIGH
    val barColor = burnoutLevel.getStrokeColor()
    val cardBgColor = burnoutLevel.getBackgroundColor()
    val interactionSource = remember { MutableInteractionSource() }

    // Animate progress
    var targetProgress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(progress) { targetProgress = progress }
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 900),
        label = "velocity_$name",
    )

    Card(
        shape = PredictaShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = PredictaShapes.medium,
                blurRadius = 0.dp,
                tintColor = barColor,
                tintAlpha = 0.08f,
                isActive = !isHealthy,
            )
            .pressScale(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(barColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    TeamAvatar(
                        avatarUrl = avatarUrl,
                        tint = barColor,
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = role,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                // Status badge
                AnimatedNumberText(
                    value = done,
                    suffix = " / $total",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = barColor,
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Подробнее",
                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Horizontal velocity bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = barColor,
                trackColor = barColor.copy(alpha = 0.12f),
                strokeCap = StrokeCap.Round,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Label under the bar
            Text(
                text = if (isHealthy) "Темп: Отличный" else "Темп: Критическое отставание",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = barColor,
            )
        }
    }
}

@Composable
private fun TeamAvatar(
    avatarUrl: String?,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    if (avatarUrl.isNullOrBlank()) {
        AvatarFallback(tint = tint, modifier = modifier)
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
private fun AvatarFallback(
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = Icons.Outlined.Person,
        contentDescription = null,
        tint = tint,
        modifier = modifier.size(24.dp),
    )
}

// ──────────────────────────────────────────────────────────────────────────────
// Summary Card
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun SummaryCard(
    employees: List<Employee>,
    modifier: Modifier = Modifier,
) {
    val total = employees.sumOf { it.totalCount }
    val done = employees.sumOf { it.doneCount }
    val overloaded = employees.count { it.burnoutRisk >= 0.7f }

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
            Text(
                text = "Общая статистика",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(12.dp))

            SummaryRow(
                label = "Всего задач в спринте",
                value = "$total",
            )
            SummaryRow(
                label = "Закрыто",
                value = "$done",
            )
            SummaryRow(
                label = "Осталось",
                value = "${(total - done).coerceAtLeast(0)}",
            )
            SummaryRow(
                label = "Перегружено",
                value = "$overloaded",
                valueColor = if (overloaded > 0) SemanticCritical else SemanticSuccess,
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (valueColor == Color.Unspecified) {
                MaterialTheme.colorScheme.primary
            } else {
                valueColor
            },
        )
    }
}

