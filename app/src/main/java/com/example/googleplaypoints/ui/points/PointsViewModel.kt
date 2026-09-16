package com.example.googleplaypoints.ui.points

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.googleplaypoints.data.model.Points
import com.example.googleplaypoints.data.repository.AuthRepository
import com.example.googleplaypoints.data.repository.PointsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PointsViewModel(
    private val pointsRepository: PointsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _totalPoints = MutableStateFlow(0L)
    val totalPoints: StateFlow<Long> = _totalPoints

    private val _recentPoints = MutableStateFlow<List<Points>>(emptyList())
    val recentPoints: StateFlow<List<Points>> = _recentPoints

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadPoints() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = authRepository.getCurrentUserId()
                if (userId != null) {
                    // Load total points
                    val total = pointsRepository.getTotalPoints(userId)
                    _totalPoints.value = total

                    // Load recent points
                    val recent = pointsRepository.getRecentPoints(userId, 10)
                    recent.onSuccess {
                        _recentPoints.value = it
                        _errorMessage.value = null
                    }.onFailure {
                        _errorMessage.value = "Failed to load points: ${it.message}"
                    }

                    // Sync with server
                    val token = authRepository.getCurrentToken()
                    if (token != null) {
                        pointsRepository.syncPoints(userId, token)
                            .onFailure {
                                // Don't show sync errors as critical
                                println("Sync error: ${it.message}")
                            }
                    }
                } else {
                    _errorMessage.value = "User not logged in"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}