package com.predicta.app.feature_tasks.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.predicta.app.data.demo.DemoStateManager
import com.predicta.app.data.demo.DemoTask
import com.predicta.app.data.demo.TaskStatus
import com.predicta.app.ui.theme.ErrorRed
import com.predicta.app.ui.theme.PredictaShapes
import com.predicta.app.ui.theme.PrimaryBlue
import com.predicta.app.ui.theme.SecondarySlate
import com.predicta.app.ui.theme.SuccessGreen
import com.predicta.app.ui.theme.SurfaceWhite
import com.predicta.app.ui.theme.TextSecondary

@Composable
fun TaskReassignmentScreen(
    taskId: String,
    demoStateManager: DemoStateManager,
    onNavigateBack: () -> Unit,
    onReassignmentComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val demoData by demoStateManager.demoState.collectAsStateWithLifecycle()
    val task = demoData.pavelTasks.find { it.id == taskId }
    var isReassigned by remember { mutableStateOf(false) }
    val canReassign = task?.status == TaskStatus.IN_PROGRESS || task?.status == TaskStatus.TODO

    if (task == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Задача не найдена",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
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
        // ── Back button + header ────────────────────────────────────────
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = PrimaryBlue,
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Перераспределение задачи",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlue,
                )
            }
        }

        // ── Task info card ──────────────────────────────────────────────
        item {
            Card(
                shape = PredictaShapes.medium,
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                modifier = Modifier
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
                    Text(
                        text = "Задача",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                    )
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }

        // ── Transfer visualization ──────────────────────────────────────
        item {
            TransferVisualization(
                fromName = demoData.pavelName,
                toName = demoData.olegName,
            )
        }

        // ── Recommended assignee ────────────────────────────────────────
        item {
            RecommendedAssigneeCard(
                name = demoData.olegName,
                role = demoData.olegRole,
                done = demoData.olegDone,
                total = demoData.olegTotal,
            )
        }

        // ── Confirm button or Success state ─────────────────────────────
        item {
            if (!canReassign && !isReassigned) {
                Text(
                    text = "Эту задачу нельзя перераспределить",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            } else if (!isReassigned) {
                Button(
                    onClick = {
                        demoStateManager.reassignTask(taskId)
                        isReassigned = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = PredictaShapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Filled.SwapHoriz,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Подтвердить перераспределение",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            } else {
                SuccessCard(onGoToDashboard = onReassignmentComplete)
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Transfer Visualization (From → To)
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun TransferVisualization(
    fromName: String,
    toName: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // From
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(ErrorRed.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = ErrorRed,
                    modifier = Modifier.size(32.dp),
                )
            }
            Text(
                text = fromName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = ErrorRed,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = "Перегружен",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
            )
        }

        // Arrow
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(32.dp),
        )

        // To
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(SuccessGreen.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(32.dp),
                )
            }
            Text(
                text = toName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = SuccessGreen,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = "Свободен",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Recommended Assignee Card
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun RecommendedAssigneeCard(
    name: String,
    role: String,
    done: Int,
    total: Int,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = PredictaShapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = SuccessGreen.copy(alpha = 0.06f),
        ),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = SuccessGreen.copy(alpha = 0.2f),
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
                    imageVector = Icons.Outlined.Star,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "Рекомендуемый исполнитель",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = SuccessGreen,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(SuccessGreen.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(28.dp),
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue,
                    )
                    Text(
                        text = role,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$done / $total",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen,
                    )
                    Text(
                        text = "задач закрыто",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                    )
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Success Card
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun SuccessCard(
    onGoToDashboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(500)) + scaleIn(tween(500)),
    ) {
        Card(
            shape = PredictaShapes.large,
            modifier = modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF2E7D32), Color(0xFF1B5E20)),
                        ),
                    )
                    .padding(28.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Задача перераспределена!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Сроки в Jira обновлены.\nПроект выровнен.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onGoToDashboard,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF1B5E20),
                        ),
                        shape = PredictaShapes.medium,
                    ) {
                        Text(
                            text = "Вернуться на дашборд",
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}
