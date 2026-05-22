package com.predicta.app.feature_tasks.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.predicta.app.feature_employees.domain.model.Employee
import com.predicta.app.feature_tasks.presentation.components.AIRecommendationCard
import com.predicta.app.ui.theme.ErrorRed
import com.predicta.app.ui.theme.PredictaShapes
import com.predicta.app.ui.theme.PrimaryBlue
import com.predicta.app.ui.theme.SecondarySlate
import com.predicta.app.ui.theme.SuccessGreen
import com.predicta.app.ui.theme.SurfaceWhite
import com.predicta.app.ui.theme.TextSecondary
import org.koin.androidx.compose.koinViewModel

@Composable
fun TaskAssignmentScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TaskContent(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskContent(
    state: TaskState,
    onEvent: (TaskEvent) -> Unit,
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = "New Task Assignment",
            style = MaterialTheme.typography.titleMedium,
            color = PrimaryBlue,
            fontWeight = FontWeight.SemiBold,
        )

        // ── Task Description Field ──────────────────────────────────────
        OutlinedTextField(
            value = state.taskDescription,
            onValueChange = { onEvent(TaskEvent.UpdateDescription(it)) },
            label = { Text("Task Description") },
            placeholder = { Text("Describe the task to be assigned…") },
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            shape = PredictaShapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = SecondarySlate.copy(alpha = 0.3f),
                cursorColor = PrimaryBlue,
                focusedLabelColor = PrimaryBlue,
                unfocusedLabelColor = TextSecondary,
            ),
            maxLines = 5,
        )

        // ── Assignee Dropdown ───────────────────────────────────────────
        Text(
            text = "Assign To",
            style = MaterialTheme.typography.labelLarge,
            color = PrimaryBlue,
            fontWeight = FontWeight.SemiBold,
        )

        ExposedDropdownMenuBox(
            expanded = state.isDropdownExpanded,
            onExpandedChange = { onEvent(TaskEvent.ToggleDropdown) },
        ) {
            OutlinedTextField(
                value = state.selectedEmployee?.name ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Select a team member") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "Expand dropdown",
                        tint = SecondarySlate,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                shape = PredictaShapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = SecondarySlate.copy(alpha = 0.3f),
                    focusedLabelColor = PrimaryBlue,
                ),
            )

            ExposedDropdownMenu(
                expanded = state.isDropdownExpanded,
                onDismissRequest = { onEvent(TaskEvent.DismissDropdown) },
            ) {
                state.employees.forEach { employee ->
                    DropdownMenuItem(
                        text = {
                            AssigneeOption(employee = employee)
                        },
                        onClick = { onEvent(TaskEvent.SelectEmployee(employee)) },
                    )
                }
            }
        }

        // ── Selected Assignee Summary Card ──────────────────────────────
        state.selectedEmployee?.let { employee ->
            AssigneeSummaryCard(employee = employee)
        }

        // ── AI Recommendation Card ──────────────────────────────────────
        AIRecommendationCard(
            recommendation = state.aiRecommendation,
            onDismiss = { onEvent(TaskEvent.DismissRecommendation) },
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Assignee Dropdown Option
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun AssigneeOption(
    employee: Employee,
    modifier: Modifier = Modifier,
) {
    val riskColor = when {
        employee.burnoutRisk > 0.7f -> ErrorRed
        employee.burnoutRisk > 0.45f -> SecondarySlate
        else -> SuccessGreen
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Avatar placeholder
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PrimaryBlue.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(20.dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = employee.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = PrimaryBlue,
            )
            Text(
                text = employee.role,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }

        // Burnout risk badge
        Text(
            text = "${(employee.burnoutRisk * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = riskColor,
        )
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Selected Assignee Summary Card
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun AssigneeSummaryCard(
    employee: Employee,
    modifier: Modifier = Modifier,
) {
    val riskColor = when {
        employee.burnoutRisk > 0.7f -> ErrorRed
        employee.burnoutRisk > 0.45f -> SecondarySlate
        else -> SuccessGreen
    }
    val riskLabel = when {
        employee.burnoutRisk > 0.7f -> "Critical"
        employee.burnoutRisk > 0.45f -> "Moderate"
        else -> "Healthy"
    }

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(28.dp),
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = employee.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBlue,
                )
                Text(
                    text = employee.role,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = riskLabel,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = riskColor,
                )
                Text(
                    text = "Workload ${(employee.workloadPercentage * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                )
            }
        }
    }
}
