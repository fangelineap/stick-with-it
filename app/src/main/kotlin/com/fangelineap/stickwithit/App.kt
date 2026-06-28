package com.fangelineap.stickwithit

import java.time.Instant

class StickWithItApp(
    private val service: WorkspaceTaskService,
) {
    fun run(args: Array<String>) {
        when (args.firstOrNull()) {
            "add-workspace" -> {
                val name = args.getOrNull(1) ?: error("Usage: add-workspace <name>")
                println(service.createWorkspace(name))
            }

            "add-task" -> {
                val workspaceId = args.getOrNull(1) ?: error("Usage: add-task <workspaceId> <title> [dueAtIsoInstant]")
                val title = args.getOrNull(2) ?: error("Usage: add-task <workspaceId> <title> [dueAtIsoInstant]")
                val dueAt = args.getOrNull(3)?.let(Instant::parse)
                println(service.addTask(workspaceId = workspaceId, title = title, dueAt = dueAt))
            }

            "list-tasks" -> service.allTasks().forEach(::println)
            "list-workspaces" -> service.allWorkspaces().forEach(::println)
            "check-alarms" -> service.runDueDateAlarmCheck().forEach { println("Alarm triggered for: ${it.title}") }
            else -> {
                println("Usage:")
                println("  add-workspace <name>")
                println("  add-task <workspaceId> <title> [dueAtIsoInstant]")
                println("  list-workspaces")
                println("  list-tasks")
                println("  check-alarms")
            }
        }
    }

    companion object {
        fun fromEnvironment(): StickWithItApp {
            val supabaseUrl = System.getenv("SUPABASE_URL")
            val supabaseKey = System.getenv("SUPABASE_ANON_KEY")

            return if (!supabaseUrl.isNullOrBlank() && !supabaseKey.isNullOrBlank()) {
                val client = SupabaseRestClient(SupabaseConfig(supabaseUrl, supabaseKey))
                val alarm = ConsoleAlarmService()
                StickWithItApp(
                    WorkspaceTaskService(
                        workspaceRepository = SupabaseWorkspaceRepository(client),
                        taskRepository = SupabaseTaskRepository(client),
                        alarmService = alarm,
                    ),
                )
            } else {
                val inMemory = InMemoryTaskStore()
                StickWithItApp(
                    WorkspaceTaskService(
                        workspaceRepository = inMemory,
                        taskRepository = inMemory,
                        alarmService = ConsoleAlarmService(),
                    ),
                )
            }
        }
    }
}

fun main(args: Array<String>) {
    StickWithItApp.fromEnvironment().run(args)
}
