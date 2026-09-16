package com.example.googleplaypoints.ui.topup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.googleplaypoints.data.model.TopUp
import com.example.googleplaypoints.data.repository.AuthRepository
import com.example.googleplaypoints.data.repository.TopUpRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TopUpViewModel(
    private val topUpRepository: TopUpRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _topUpHistory = MutableStateFlow<List<TopUp>>(emptyList())
    val topUpHistory: StateFlow<List<TopUp>> = _topUpHistory

    private val _topUpInProgress = MutableStateFlow<TopUp?>(null)
    val topUpInProgress: StateFlow<TopUp?> = _topUpInProgress

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadTopUpHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = authRepository.getCurrentUserId()
                if (userId != null) {
                    topUpRepository.getTopUpsByUserId(userId)
                        .collect { topUps ->
                            _topUpHistory.value = topUps
                            _errorMessage.value = null
                        }
                } else {
                    _errorMessage.value = "User not logged in"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load history: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun initiateTopUp(
        packageName: String,
        appName: String,
        amount: Double
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = authRepository.getCurrentUserId()
                val token = authRepository.getCurrentToken()

                if (userId == null || token == null) {
                    _errorMessage.value = "User not logged in"
                    _isLoading.value = false
                    return@launch
                }

                if (amount <= 0) {
                    _errorMessage.value = "Please enter a valid amount"
                    _isLoading.value = false
                    return@launch
                }

                val result = topUpRepository.initiateTopUp(
                    userId = userId,
                    packageName = packageName,
                    appName = appName,
                    amount = amount,
                    token = token
                )

                result.onSuccess { topUp ->
                    _topUpInProgress.value = topUp
                    _successMessage.value = "Top-up initiated successfully"
                    _errorMessage.value = null
                    loadTopUpHistory()
                }.onFailure { error ->
                    _errorMessage.value = "Failed to initiate top-up: ${error.message}"
                    _successMessage.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                _successMessage.value = null
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun verifyTopUp(transactionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = authRepository.getCurrentToken()
                if (token == null) {
                    _errorMessage.value = "Authentication required"
                    _isLoading.value = false
                    return@launch
                }

                val result = topUpRepository.verifyAndCompleteTopUp(transactionId, token)

                result.onSuccess { topUp ->
                    _topUpInProgress.value = topUp
                    _successMessage.value = "Top-up completed successfully!"
                    _errorMessage.value = null
                    loadTopUpHistory()
                }.onFailure { error ->
                    _errorMessage.value = "Verification failed: ${error.message}"
                    _successMessage.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                _successMessage.value = null
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    fun clearTopUpInProgress() {
        _topUpInProgress.value = null
    }
}