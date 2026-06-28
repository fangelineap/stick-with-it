package com.stickwithit.data.repository

import android.util.Log
import com.stickwithit.data.model.LoginResult
import io.github.jan.supabase.exceptions.RestException
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
            Result.failure(Exception(e.toUserMessage()))
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
            Result.failure(Exception(e.toUserMessage()))
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

    private fun Exception.toUserMessage(): String {
        val raw = if (this is RestException) error ?: message else message
        return when {
            raw == null -> "Something went wrong. Please try again."
            raw.contains("invalid_credentials", ignoreCase = true)
                    || raw.contains("Invalid login credentials", ignoreCase = true) ->
                "Incorrect email or password."
            raw.contains("email_not_confirmed", ignoreCase = true)
                    || raw.contains("Email not confirmed", ignoreCase = true) ->
                "Please verify your email before signing in."
            raw.contains("user_already_exists", ignoreCase = true)
                    || raw.contains("User already registered", ignoreCase = true) ->
                "An account with this email already exists."
            raw.contains("weak_password", ignoreCase = true) ->
                "Password is too weak. Use at least 6 characters."
            raw.contains("email_address_invalid", ignoreCase = true)
                    || raw.contains("Unable to validate email address", ignoreCase = true) ->
                "Please enter a valid email address."
            raw.contains("over_email_send_rate_limit", ignoreCase = true)
                    || raw.contains("rate limit", ignoreCase = true) ->
                "Too many attempts. Please wait a moment and try again."
            raw.contains("network", ignoreCase = true)
                    || raw.contains("Unable to resolve host", ignoreCase = true)
                    || raw.contains("timeout", ignoreCase = true) ->
                "No internet connection. Please check your network."
            else -> "Something went wrong. Please try again."
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
