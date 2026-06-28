package com.fangelineap.stickwithit

import java.time.Instant
import java.util.UUID

interface WorkspaceRepository {
    fun create(name: String): Workspace
    fun list(): List<Workspace>
}

interface TaskRepository {
    fun create(workspaceId: String, title: String, dueAt: Instant?): TaskItem
    fun listAll(): List<TaskItem>
    fun listByWorkspace(workspaceId: String): List<TaskItem>
    fun markCompleted(taskId: String): Boolean
}

class InMemoryTaskStore : WorkspaceRepository, TaskRepository {
    private val workspaces = linkedMapOf<String, Workspace>()
    private val tasks = linkedMapOf<String, TaskItem>()

    override fun create(name: String): Workspace {
        val workspace = Workspace(id = UUID.randomUUID().toString(), name = name)
        workspaces[workspace.id] = workspace
        return workspace
    }

    override fun list(): List<Workspace> = workspaces.values.toList()

    override fun create(workspaceId: String, title: String, dueAt: Instant?): TaskItem {
        require(workspaces.containsKey(workspaceId)) { "Workspace '$workspaceId' does not exist" }

        val task = TaskItem(
            id = UUID.randomUUID().toString(),
            workspaceId = workspaceId,
            title = title,
            dueAt = dueAt,
            completed = false,
        )
        tasks[task.id] = task
        return task
    }

    override fun listAll(): List<TaskItem> = tasks.values.toList()

    override fun listByWorkspace(workspaceId: String): List<TaskItem> = tasks.values.filter { it.workspaceId == workspaceId }

    override fun markCompleted(taskId: String): Boolean {
        val existing = tasks[taskId] ?: return false
        tasks[taskId] = existing.copy(completed = true)
        return true
    }
}
