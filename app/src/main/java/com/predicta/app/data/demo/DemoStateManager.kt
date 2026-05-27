package com.predicta.app.data.demo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Single source of truth for the hackathon demo scenario.
 *
 * This singleton simulates backend state changes in real-time:
 * - Sprint status (delayed vs on-track)
 * - Employee velocity (Oleg 5/5, Pavel 1/5)
 * - Pavel's task list (with reassignment capability)
 * - Oleg's assigned-but-not-yet-done overflow tasks
 *
 * All screens observe [demoState] and react to changes automatically.
 */
class DemoStateManager {

    private val _demoState = MutableStateFlow(createInitialState())
    val demoState: StateFlow<DemoDataDto> = _demoState.asStateFlow()

    /**
     * Reassign a task from Pavel to Oleg.
     * Updates workload, sprint status, and keeps the new assignee's task open.
     */
    fun reassignTask(taskId: String) {
        _demoState.update { current ->
            val hasMatchingOpenTask = current.pavelTasks.any { task ->
                task.id == taskId &&
                    (task.status == TaskStatus.IN_PROGRESS || task.status == TaskStatus.TODO)
            }
            if (!hasMatchingOpenTask) return@update current

            val updatedPavelTasks = current.pavelTasks.map { task ->
                if (task.id == taskId) task.copy(
                    status = TaskStatus.REASSIGNED,
                    assigneeId = OLEG_ID,
                    assigneeName = "Олег",
                ) else task
            }

            // Recalculate: Pavel loses one open task, Oleg gains one open task.
            val reassignedCount = updatedPavelTasks.count { it.status == TaskStatus.REASSIGNED }
            val pavelTotal = PAVEL_INITIAL_TOTAL - reassignedCount
            val olegTotal = OLEG_INITIAL_TOTAL + reassignedCount

            // If Pavel's remaining load is manageable, project is on track
            val pavelRemaining = pavelTotal - PAVEL_INITIAL_DONE
            val isProjectOnTrack = pavelRemaining <= current.pavelDeadlineDays

            current.copy(
                isProjectDelayed = !isProjectOnTrack,
                delayDays = if (isProjectOnTrack) 0 else current.delayDays,
                sprintCompletionPercent = calculateCompletion(
                    olegDone = OLEG_INITIAL_DONE,
                    olegTotal = olegTotal,
                    pavelDone = PAVEL_INITIAL_DONE,
                    pavelTotal = pavelTotal,
                ),
                olegDone = OLEG_INITIAL_DONE,
                olegTotal = olegTotal,
                pavelTotal = pavelTotal,
                pavelTasks = updatedPavelTasks,
                hasBeenReassigned = true,
            )
        }
    }

    fun toggleDeepWork() {
        _demoState.update { current ->
            current.copy(isDeepWorkActive = !current.isDeepWorkActive)
        }
    }

    private fun calculateCompletion(
        olegDone: Int,
        olegTotal: Int,
        pavelDone: Int,
        pavelTotal: Int,
    ): Float {
        val totalTasks = olegTotal + pavelTotal
        val totalDone = olegDone + pavelDone
        return if (totalTasks > 0) totalDone.toFloat() / totalTasks else 0f
    }

    companion object {
        const val OLEG_ID = "emp_oleg"
        const val PAVEL_ID = "emp_pavel"

        private const val OLEG_INITIAL_DONE = 5
        private const val OLEG_INITIAL_TOTAL = 5
        private const val PAVEL_INITIAL_DONE = 1
        private const val PAVEL_INITIAL_TOTAL = 5

        private fun createInitialState(): DemoDataDto {
            return DemoDataDto(
                sprintNumber = 5,
                sprintName = "Спринт №5",
                isProjectDelayed = true,
                delayDays = 3,
                delayTrack = "бэкенда",
                sprintCompletionPercent = 0.6f, // 6 out of 10 done
                sprintTotalDays = 10,
                sprintElapsedDays = 7,

                olegId = OLEG_ID,
                olegName = "Олег",
                olegRole = "Backend-разработчик",
                olegDone = OLEG_INITIAL_DONE,
                olegTotal = OLEG_INITIAL_TOTAL,

                pavelId = PAVEL_ID,
                pavelName = "Павел",
                pavelRole = "Backend-разработчик",
                pavelDone = PAVEL_INITIAL_DONE,
                pavelTotal = PAVEL_INITIAL_TOTAL,

                pavelTasks = listOf(
                    DemoTaskDto(
                        id = "task_001",
                        title = "Миграция базы данных",
                        status = TaskStatus.DONE,
                        assigneeId = PAVEL_ID,
                        assigneeName = "Павел",
                    ),
                    DemoTaskDto(
                        id = "task_002",
                        title = "Интеграция GigaChat API",
                        status = TaskStatus.IN_PROGRESS,
                        assigneeId = PAVEL_ID,
                        assigneeName = "Павел",
                    ),
                    DemoTaskDto(
                        id = "task_003",
                        title = "Настройка авторизации (OAuth2)",
                        status = TaskStatus.TODO,
                        assigneeId = PAVEL_ID,
                        assigneeName = "Павел",
                    ),
                    DemoTaskDto(
                        id = "task_004",
                        title = "REST-эндпоинты для аналитики",
                        status = TaskStatus.TODO,
                        assigneeId = PAVEL_ID,
                        assigneeName = "Павел",
                    ),
                    DemoTaskDto(
                        id = "task_005",
                        title = "Тестирование Velocity Engine",
                        status = TaskStatus.TODO,
                        assigneeId = PAVEL_ID,
                        assigneeName = "Павел",
                    ),
                ),

                pavelAiInsight = "Падение темпа работы Павла (закрыта 1 из 5 задач) обусловлено " +
                    "критическим выгоранием и перегрузкой. Анализ чата подтверждает " +
                    "систематические ночные переработки на фоне личных проблем. " +
                    "Рекомендуется временно снизить нагрузку и перераспределить часть " +
                    "его задач на свободных участников команды для сохранения сроков спринта.",

                pavelPredictedDays = 8,
                pavelDeadlineDays = 3,
                pavelRiskFactors = listOf("+12ч на выходных (GitHub)", "8 встреч подряд (Calendar)"),

                hasBeenReassigned = false,
                isDeepWorkActive = false,
            )
        }
    }
}

/**
 * Complete snapshot of the demo scenario state.
 */
data class DemoDataDto(
    // Sprint
    val sprintNumber: Int,
    val sprintName: String,
    val isProjectDelayed: Boolean,
    val delayDays: Int,
    val delayTrack: String,
    val sprintCompletionPercent: Float,
    val sprintTotalDays: Int,
    val sprintElapsedDays: Int,

    // Oleg
    val olegId: String,
    val olegName: String,
    val olegRole: String,
    val olegDone: Int,
    val olegTotal: Int,

    // Pavel
    val pavelId: String,
    val pavelName: String,
    val pavelRole: String,
    val pavelDone: Int,
    val pavelTotal: Int,

    // Pavel's tasks
    val pavelTasks: List<DemoTaskDto>,

    // AI insight for Pavel
    val pavelAiInsight: String,
    val pavelPredictedDays: Int,
    val pavelDeadlineDays: Int,
    val pavelRiskFactors: List<String>,

    // Global flag
    val hasBeenReassigned: Boolean,

    // Employee status
    val isDeepWorkActive: Boolean,
)

data class DemoTaskDto(
    val id: String,
    val title: String,
    val status: TaskStatus,
    val assigneeId: String,
    val assigneeName: String,
)

enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    DONE,
    REASSIGNED,
}
