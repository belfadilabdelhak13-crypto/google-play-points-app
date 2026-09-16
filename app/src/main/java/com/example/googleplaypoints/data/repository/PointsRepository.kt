package com.example.googleplaypoints.data.repository

import com.example.googleplaypoints.data.local.PointsDao
import com.example.googleplaypoints.data.model.Points
import com.example.googleplaypoints.data.remote.ApiService
import kotlinx.coroutines.flow.Flow

class PointsRepository(
    private val pointsDao: PointsDao,
    private val apiService: ApiService
) {
    fun getPointsByUserId(userId: String): Flow<List<Points>> =
        pointsDao.getPointsByUserId(userId)

    suspend fun getTotalPoints(userId: String): Long =
        pointsDao.getTotalPoints(userId) ?: 0L

    suspend fun syncPoints(userId: String, token: String) = runCatching {
        val response = apiService.getUserPoints(userId, "Bearer $token")
        response.recentTransactions.forEach { transaction ->
            // Parse and insert transaction data
        }
    }

    suspend fun addPoints(points: Points) {
        pointsDao.insertPoints(points)
    }
}