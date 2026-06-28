package com.fangelineap.stickwithit

import java.time.Instant

data class Workspace(
    val id: String,
    val name: String,
)

data class TaskItem(
    val id: String,
    val workspaceId: String,
    val title: String,
    val dueAt: Instant?,
    val completed: Boolean,
)
