package com.predicta.app.feature_dashboard.data.mapper

import com.predicta.app.data.demo.DemoDataDto
import com.predicta.app.data.demo.DemoTaskDto
import com.predicta.app.data.demo.TaskStatus
import com.predicta.app.feature_dashboard.domain.model.DashboardSnapshot
import com.predicta.app.feature_dashboard.domain.model.DashboardTask
import com.predicta.app.feature_dashboard.domain.model.DashboardTaskStatus

fun DemoDataDto.toDomain(): DashboardSnapshot {
    return DashboardSnapshot(
        sprintNumber = sprintNumber,
        sprintName = sprintName,
        isProjectDelayed = isProjectDelayed,
        delayDays = delayDays,
        delayTrack = delayTrack,
        sprintCompletionPercent = sprintCompletionPercent,
        sprintTotalDays = sprintTotalDays,
        sprintElapsedDays = sprintElapsedDays,
        olegId = olegId,
        olegName = olegName,
        olegRole = olegRole,
        olegDone = olegDone,
        olegTotal = olegTotal,
        pavelId = pavelId,
        pavelName = pavelName,
        pavelRole = pavelRole,
        pavelDone = pavelDone,
        pavelTotal = pavelTotal,
        pavelTasks = pavelTasks.map { it.toDomain() },
        pavelAiInsight = pavelAiInsight,
        pavelPredictedDays = pavelPredictedDays,
        pavelDeadlineDays = pavelDeadlineDays,
        pavelRiskFactors = pavelRiskFactors,
        hasBeenReassigned = hasBeenReassigned,
        isDeepWorkActive = isDeepWorkActive,
    )
}

private fun DemoTaskDto.toDomain(): DashboardTask {
    return DashboardTask(
        id = id,
        title = title,
        status = status.toDomain(),
        assigneeId = assigneeId,
        assigneeName = assigneeName,
    )
}

private fun TaskStatus.toDomain(): DashboardTaskStatus {
    return when (this) {
        TaskStatus.TODO -> DashboardTaskStatus.TODO
        TaskStatus.IN_PROGRESS -> DashboardTaskStatus.IN_PROGRESS
        TaskStatus.DONE -> DashboardTaskStatus.DONE
        TaskStatus.REASSIGNED -> DashboardTaskStatus.REASSIGNED
    }
}
