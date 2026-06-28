package com.stickwithit.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stickwithit.data.repository.AuthRepositoryImpl
import com.stickwithit.data.state.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepositoryImpl()

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState

    private val _logoutState = MutableStateFlow<AuthState>(AuthState.Idle)
    val logoutState: StateFlow<AuthState> = _logoutState

    private val _sessionState = MutableStateFlow<AuthState>(AuthState.Loading)
    val sessionState: StateFlow<AuthState> = _sessionState

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    fun checkSession() {
        viewModelScope.launch {
            _sessionState.value = AuthState.Loading
            val result = repository.checkSession()
            _sessionState.value = if (result.isSuccess) {
                val loginResult = result.getOrNull()
                if (loginResult != null) {
                    _userRole.value = loginResult.role
                    AuthState.Authenticated(loginResult.role)
                } else {
                    AuthState.Unauthenticated
                }
            } else {
                AuthState.Unauthenticated
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            val result = repository.login(email, password)
            if (result.isSuccess) {
                _userRole.value = result.getOrNull()?.role
                _loginState.value = AuthState.Success()
            } else {
                _loginState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun register(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            _registerState.value = AuthState.Loading
            val result = repository.register(email, password, fullName)
            _registerState.value = if (result.isSuccess) {
                AuthState.Success("Registration successful! Please check your email.")
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _logoutState.value = AuthState.Loading
            val result = repository.logout()
            if (result.isSuccess) {
                _userRole.value = null
                _logoutState.value = AuthState.Success()
            } else {
                _logoutState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Logout failed")
            }
        }
    }

    fun resetLoginState() { _loginState.value = AuthState.Idle }
    fun resetRegisterState() { _registerState.value = AuthState.Idle }
    fun resetLogoutState() { _logoutState.value = AuthState.Idle }
}
