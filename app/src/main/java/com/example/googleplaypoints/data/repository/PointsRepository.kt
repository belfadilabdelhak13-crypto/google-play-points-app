package com.example.googleplaypoints.data.repository

import com.example.googleplaypoints.data.local.PointsDao
import com.example.googleplaypoints.data.local.UserDao
import com.example.googleplaypoints.data.model.Points
import com.example.googleplaypoints.data.model.PointsType
import com.example.googleplaypoints.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PointsRepository(
    private val pointsDao: PointsDao,
    private val userDao: UserDao,
    private val apiService: ApiService
) {
    fun getPointsByUserId(userId: String): Flow<List<Points>> =
        pointsDao.getPointsByUserId(userId)
            .catch { e ->
                e.printStackTrace()
                emit(emptyList())
            }

    suspend fun getTotalPoints(userId: String): Long = withContext(Dispatchers.IO) {
        try {
            pointsDao.getTotalPoints(userId) ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    suspend fun syncPoints(userId: String, token: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUserPoints(userId, "Bearer $token")
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun addPoints(
        userId: String,
        pointsAmount: Long,
        pointsType: PointsType = PointsType.REWARD,
        description: String = ""
    ): Result<Points> = withContext(Dispatchers.IO) {
        try {
            if (pointsAmount <= 0) {
                return@withContext Result.failure(IllegalArgumentException("Points amount must be positive"))
            }

            val points = Points(
                userId = userId,
                pointsAmount = pointsAmount,
                pointsType = pointsType,
                description = description
            )
            pointsDao.insertPoints(points)

            // Update user's total points
            val user = userDao.getUserById(userId)
            if (user != null) {
                val newTotalPoints = getTotalPoints(userId)
                userDao.updateUser(user.copy(totalPoints = newTotalPoints))
            }

            Result.success(points)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getRecentPoints(userId: String, limit: Int = 10): Result<List<Points>> = 
        withContext(Dispatchers.IO) {
            try {
                val points = pointsDao.getRecentPoints(userId, limit)
                Result.success(points)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }

    suspend fun deletePointsByUserId(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            pointsDao.deletePointsByUserId(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}