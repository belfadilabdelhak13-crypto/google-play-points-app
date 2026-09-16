package com.example.googleplaypoints.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.googleplaypoints.data.model.User
import com.example.googleplaypoints.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = authRepository.loginWithGoogle(idToken)
                result.onSuccess { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Success
                    _errorMessage.value = null
                }.onFailure { error ->
                    _errorMessage.value = error.message ?: "Login failed"
                    _authState.value = AuthState.Error(error.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An error occurred"
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
                e.printStackTrace()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
                _currentUser.value = null
                _authState.value = AuthState.Idle
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Logout failed: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    fun checkAuthStatus() {
        viewModelScope.launch {
            try {
                val isLoggedIn = authRepository.isUserLoggedIn()
                if (isLoggedIn) {
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Idle
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}