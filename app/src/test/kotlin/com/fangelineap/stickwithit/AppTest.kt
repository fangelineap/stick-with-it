package com.fangelineap.stickwithit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

class AppTest {
    @Test
    fun `tracks tasks across all workspaces`() {
        val store = InMemoryTaskStore()
        val service = WorkspaceTaskService(store, store, NoopAlarmService())

        val home = service.createWorkspace("home")
        val work = service.createWorkspace("work")

        service.addTask(home.id, "Buy groceries", null)
        service.addTask(work.id, "Prepare standup", null)

        assertEquals(2, service.allTasks().size)
        assertEquals(1, service.tasksByWorkspace(home.id).size)
        assertEquals(1, service.tasksByWorkspace(work.id).size)
    }

    @Test
    fun `rings alarm for due tasks only`() {
        val store = InMemoryTaskStore()
        val recordingAlarm = RecordingAlarmService()
        val service = WorkspaceTaskService(store, store, recordingAlarm)

        val workspace = service.createWorkspace("alerts")
        val dueTask = service.addTask(workspace.id, "Due now", Instant.parse("2026-01-01T00:00:00Z"))
        service.addTask(workspace.id, "Due later", Instant.parse("2026-12-31T00:00:00Z"))

        val triggered = service.runDueDateAlarmCheck(Instant.parse("2026-01-01T00:00:00Z"))

        assertEquals(listOf(dueTask.id), triggered.map { it.id })
        assertEquals(listOf(dueTask.id), recordingAlarm.rungTaskIds)

        assertTrue(service.markTaskCompleted(dueTask.id))
        val retriggered = service.runDueDateAlarmCheck(Instant.parse("2026-01-02T00:00:00Z"))
        assertTrue(retriggered.none { it.id == dueTask.id })
    }

    private class RecordingAlarmService : AlarmService {
        val rungTaskIds = mutableListOf<String>()

        override fun ring(task: TaskItem) {
            rungTaskIds += task.id
        }
    }

    private class NoopAlarmService : AlarmService {
        override fun ring(task: TaskItem) = Unit
    }
}
