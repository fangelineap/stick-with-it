package com.stickwithit.data.state

sealed class WorkspaceState {
    object Idle : WorkspaceState()
    object Loading : WorkspaceState()
    data class Success(val message: String = "") : WorkspaceState()
    data class Error(val message: String) : WorkspaceState()
}