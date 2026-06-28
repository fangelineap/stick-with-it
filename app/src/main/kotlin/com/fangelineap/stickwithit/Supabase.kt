package com.fangelineap.stickwithit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.UUID

data class SupabaseConfig(
    val url: String,
    val anonKey: String,
)

class SupabaseRestClient(
    private val config: SupabaseConfig,
    private val httpClient: HttpClient = HttpClient.newHttpClient(),
    private val json: Json = Json { ignoreUnknownKeys = true },
) {
    fun createWorkspace(name: String): Workspace {
        val payload = json.encodeToString(listOf(WorkspaceRecord(id = UUID.randomUUID().toString(), name = name)))
        return post("workspaces", payload) { body ->
            val rows = json.decodeFromString<List<WorkspaceRecord>>(body)
            rows.first().toDomain()
        }
    }

    fun listWorkspaces(): List<Workspace> =
        get("workspaces?select=id,name") { body ->
            json.decodeFromString<List<WorkspaceRecord>>(body).map(WorkspaceRecord::toDomain)
        }

    fun createTask(workspaceId: String, title: String, dueAt: Instant?): TaskItem {
        val payload = json.encodeToString(
            listOf(
                TaskRecord(
                    id = UUID.randomUUID().toString(),
                    workspaceId = workspaceId,
                    title = title,
                    dueAt = dueAt?.toString(),
                    completed = false,
                ),
            ),
        )

        return post("tasks", payload) { body ->
            val rows = json.decodeFromString<List<TaskRecord>>(body)
            rows.first().toDomain()
        }
    }

    fun listTasks(): List<TaskItem> =
        get("tasks?select=id,workspace_id,title,due_at,completed") { body ->
            json.decodeFromString<List<TaskRecord>>(body).map(TaskRecord::toDomain)
        }

    fun listTasksByWorkspace(workspaceId: String): List<TaskItem> {
        val encodedWorkspace = URLEncoder.encode("eq.$workspaceId", StandardCharsets.UTF_8)
        return get("tasks?select=id,workspace_id,title,due_at,completed&workspace_id=$encodedWorkspace") { body ->
            json.decodeFromString<List<TaskRecord>>(body).map(TaskRecord::toDomain)
        }
    }

    fun markCompleted(taskId: String): Boolean {
        val encodedTask = URLEncoder.encode("eq.$taskId", StandardCharsets.UTF_8)
        val payload = "{\"completed\":true}"

        return patch("tasks?id=$encodedTask", payload) { responseBody ->
            json.decodeFromString<List<TaskRecord>>(responseBody).isNotEmpty()
        }
    }

    private fun <T> get(path: String, transform: (String) -> T): T {
        val request = baseBuilder(path)
            .GET()
            .build()
        return send(request, transform)
    }

    private fun <T> post(path: String, payload: String, transform: (String) -> T): T {
        val request = baseBuilder(path)
            .header("Content-Type", "application/json")
            .header("Prefer", "return=representation")
            .POST(HttpRequest.BodyPublishers.ofString(payload))
            .build()
        return send(request, transform)
    }

    private fun <T> patch(path: String, payload: String, transform: (String) -> T): T {
        val request = baseBuilder(path)
            .header("Content-Type", "application/json")
            .header("Prefer", "return=representation")
            .method("PATCH", HttpRequest.BodyPublishers.ofString(payload))
            .build()
        return send(request, transform)
    }

    private fun baseBuilder(path: String): HttpRequest.Builder =
        HttpRequest.newBuilder(URI.create("${config.url.trimEnd('/')}/rest/v1/$path"))
            .header("apikey", config.anonKey)

    private fun <T> send(request: HttpRequest, transform: (String) -> T): T {
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() !in 200..299) {
            error("Supabase request failed (${response.statusCode()}): ${response.body()}")
        }
        return transform(response.body())
    }
}

class SupabaseWorkspaceRepository(
    private val client: SupabaseRestClient,
) : WorkspaceRepository {
    override fun create(name: String): Workspace = client.createWorkspace(name)

    override fun list(): List<Workspace> = client.listWorkspaces()
}

class SupabaseTaskRepository(
    private val client: SupabaseRestClient,
) : TaskRepository {
    override fun create(workspaceId: String, title: String, dueAt: Instant?): TaskItem =
        client.createTask(workspaceId = workspaceId, title = title, dueAt = dueAt)

    override fun listAll(): List<TaskItem> = client.listTasks()

    override fun listByWorkspace(workspaceId: String): List<TaskItem> = client.listTasksByWorkspace(workspaceId)

    override fun markCompleted(taskId: String): Boolean = client.markCompleted(taskId)
}

@Serializable
private data class WorkspaceRecord(
    val id: String,
    val name: String,
) {
    fun toDomain(): Workspace = Workspace(id = id, name = name)
}

@Serializable
private data class TaskRecord(
    val id: String,
    @SerialName("workspace_id") val workspaceId: String,
    val title: String,
    @SerialName("due_at") val dueAt: String? = null,
    val completed: Boolean = false,
) {
    fun toDomain(): TaskItem = TaskItem(
        id = id,
        workspaceId = workspaceId,
        title = title,
        dueAt = dueAt?.let(Instant::parse),
        completed = completed,
    )
}
