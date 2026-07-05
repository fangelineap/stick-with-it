package com.stickwithit.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stickwithit.data.model.WorkspaceResult
import com.stickwithit.data.repository.WorkspaceRepositoryImpl
import com.stickwithit.data.state.WorkspaceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WorkspaceViewModel: ViewModel() {
    private val repository = WorkspaceRepositoryImpl()
    private val _loadingState = MutableStateFlow<WorkspaceState>(WorkspaceState.Idle)
    val loadingSatte: StateFlow<WorkspaceState> = _loadingState

    private val _workspaces = MutableStateFlow<List<WorkspaceResult>>(emptyList())
    val workspaces: StateFlow<List<WorkspaceResult>> = _workspaces

    private val _fetchState = MutableStateFlow<WorkspaceState>(WorkspaceState.Idle)
    val fetchState: StateFlow<WorkspaceState> = _fetchState

    fun fetchWorkspaces() {
        viewModelScope.launch {
            _fetchState.value = WorkspaceState.Loading
            val result = repository.fetchWorkspaces()
            _fetchState.value = if (result.isSuccess) {
                _workspaces.value = result.getOrNull()?.data ?: emptyList()
                WorkspaceState.Success()
            } else {
                WorkspaceState.Error(result.exceptionOrNull()?.message ?: "Failed to load workspaces")
            }
        }
    }

    fun addWorkspace(name: String) {
        viewModelScope.launch {
            _loadingState.value = WorkspaceState.Loading
            val result = repository.addWorkspace(name)
            _loadingState.value = if (result.isSuccess) {
                val message = result.getOrNull()?.message ?: "Workspace added"
                WorkspaceState.Success(message)
            } else {
                WorkspaceState.Error(result.exceptionOrNull()?.message ?: "Failed to add workspace")
            }
        }
    }

    fun updateWorkspace(id: String, name: String) {
        viewModelScope.launch {
            _loadingState.value = WorkspaceState.Loading
            val result = repository.updateWorkspace(id, name)
            _loadingState.value = if (result.isSuccess) {
                val message = result.getOrNull()?.message ?: "Workspace updated"
                WorkspaceState.Success(message)
            } else {
                WorkspaceState.Error(result.exceptionOrNull()?.message ?: "Failed to update workspace")
            }
        }
    }

    fun deleteWorkspace(id: String) {
        viewModelScope.launch {
            _loadingState.value = WorkspaceState.Loading
            val result = repository.deleteWorkspace(id)
            _loadingState.value = if (result.isSuccess) {
                val message = result.getOrNull()?.message ?: "Workspace deleted"
                WorkspaceState.Success(message)
            } else {
                WorkspaceState.Error(result.exceptionOrNull()?.message ?: "Failed to delete workspace")
            }
        }
    }

    fun resetLoadingState() { _loadingState.value = WorkspaceState.Idle }
}