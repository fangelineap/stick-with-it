package com.stickwithit.data.model

import kotlinx.serialization.Serializable

@Serializable
data class WorkspaceResult (
    val id: String,
    val name: String,
    val ownerId: String,
    val createdAt: String,
    val updatedAt: String? = null
)