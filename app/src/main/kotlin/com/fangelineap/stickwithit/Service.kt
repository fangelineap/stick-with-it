package com.fangelineap.stickwithit

import java.time.Clock
import java.time.Instant

interface AlarmService {
    fun ring(task: TaskItem)
}

class ConsoleAlarmService : AlarmService {
    override fun ring(task: TaskItem) {
        println("🔔 DUE TASK: ${task.title} (workspace=${task.workspaceId}, dueAt=${task.dueAt})")
    }
}

class WorkspaceTaskService(
    private val workspaceRepository: WorkspaceRepository,
    private val taskRepository: TaskRepository,
    private val alarmService: AlarmService,
    private val clock: Clock = Clock.systemUTC(),
) {
    fun createWorkspace(name: String): Workspace = workspaceRepository.create(name)

    fun allWorkspaces(): List<Workspace> = workspaceRepository.list()

    fun addTask(workspaceId: String, title: String, dueAt: Instant?): TaskItem =
        taskRepository.create(workspaceId = workspaceId, title = title, dueAt = dueAt)

    fun allTasks(): List<TaskItem> = taskRepository.listAll()

    fun tasksByWorkspace(workspaceId: String): List<TaskItem> = taskRepository.listByWorkspace(workspaceId)

    fun markTaskCompleted(taskId: String): Boolean = taskRepository.markCompleted(taskId)

    fun runDueDateAlarmCheck(now: Instant = clock.instant()): List<TaskItem> {
        val dueTasks = taskRepository
            .listAll()
            .filter { task -> !task.completed && task.dueAt != null && !task.dueAt.isAfter(now) }

        dueTasks.forEach(alarmService::ring)
        return dueTasks
    }
}
