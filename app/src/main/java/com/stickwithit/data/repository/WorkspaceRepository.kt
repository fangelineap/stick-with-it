package com.stickwithit.data.repository

import android.util.Log
import com.stickwithit.data.model.SupabaseFetchResult
import com.stickwithit.data.model.SupabaseOperationsResult
import com.stickwithit.data.model.WorkspaceResult
import com.stickwithit.data.supabase.supabase
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

interface WorkspaceRepository {
    suspend fun fetchWorkspaces(): Result<SupabaseFetchResult<List<WorkspaceResult>>>
    suspend fun addWorkspace(name: String): Result<SupabaseOperationsResult>
    suspend fun updateWorkspace(id: String, name: String): Result<SupabaseOperationsResult>
    suspend fun deleteWorkspace(id: String): Result<SupabaseOperationsResult>
}

class WorkspaceRepositoryImpl : WorkspaceRepository {
    override suspend fun fetchWorkspaces(): Result<SupabaseFetchResult<List<WorkspaceResult>>> {
        return try {
            val result = supabase.postgrest
                .rpc("fetch_workspaces")
                .decodeAs<SupabaseFetchResult<List<WorkspaceResult>>>()
            Result.success(result)
        } catch (e: Exception) {
            Log.e("WorkspaceRepository", "fetchWorkspaces failed", e)
            Result.failure(e)
        }
    }

    override suspend fun addWorkspace(name: String): Result<SupabaseOperationsResult> {
        return try {
            // owner_id is resolved server-side (current_profile_id()) inside the RPC.
            supabase.postgrest.rpc(
                function = "add_workspace",
                parameters = buildJsonObject { put("p_name", name) }
            )
            Result.success(SupabaseOperationsResult(true, "Workspace added"))
        } catch (e: Exception) {
            Log.e("WorkspaceRepository", "addWorkspace failed", e)
            Result.failure(e)
        }
    }

    override suspend fun updateWorkspace(id: String, name: String): Result<SupabaseOperationsResult> {
        return try {
            supabase.postgrest
                .from("workspaces")
                .update(mapOf("name" to name)) {
                    filter { eq("id", id) }
                }
            Result.success(SupabaseOperationsResult(true, "Workspace updated"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteWorkspace(id: String): Result<SupabaseOperationsResult> {
        return try {
            supabase.postgrest
                .from("workspaces")
                .delete {
                    filter { eq("id", id) }
                }
            Result.success(SupabaseOperationsResult(true, "Workspace deleted"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}