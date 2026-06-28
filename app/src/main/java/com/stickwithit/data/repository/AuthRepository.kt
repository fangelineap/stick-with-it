package com.stickwithit.data.repository

import android.util.Log
import com.stickwithit.data.model.LoginResult
import com.stickwithit.data.supabase.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<LoginResult>
    suspend fun register(email: String, password: String, fullName: String, role: String? = "user"): Result<Unit>
    suspend fun logout(): Result<Unit>
    suspend fun checkSession(): Result<LoginResult?>
}

class AuthRepositoryImpl : AuthRepository {

    override suspend fun login(email: String, password: String): Result<LoginResult> {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val user = supabase.auth.currentUserOrNull()
            if (user != null) {
                val role = user.userMetadata?.get("role")?.jsonPrimitive?.contentOrNull ?: "user"
                Result.success(LoginResult(user.id, user.email ?: "", role))
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, password: String, fullName: String, role: String?): Result<Unit> {
        return try {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("role", role)
                    put("full_name", fullName)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            supabase.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkSession(): Result<LoginResult?> {
        return try {
            supabase.auth.awaitInitialization()
            val user = supabase.auth.currentUserOrNull()
            if (user != null) {
                Log.d("AuthRepository", "Already Logged In, User ID: ${user.id}, Email: ${user.email}, Role: ${user.userMetadata?.get("role")}")
                val role = user.userMetadata?.get("role")?.jsonPrimitive?.contentOrNull ?: "user"
                Result.success(LoginResult(user.id, user.email ?: "", role))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
