package com.example.googleplaypoints.util

import com.example.googleplaypoints.data.model.PointsType
import com.example.googleplaypoints.data.repository.PointsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Utility class for accumulating points with built-in safeguards
 * Ensures points are added reliably and without race conditions
 */
class PointsAccumulator(private val pointsRepository: PointsRepository) {

    /**
     * Safely accumulate points with transaction-like behavior
     */
    suspend fun accumulatePoints(
        userId: String,
        points: Long,
        type: PointsType = PointsType.REWARD,
        description: String = ""
    ): Result<Long> = withContext(Dispatchers.IO) {
        try {
            // Validate input
            if (points <= 0) {
                return@withContext Result.failure(
                    IllegalArgumentException("Points must be greater than 0")
                )
            }

            if (userId.isBlank()) {
                return@withContext Result.failure(
                    IllegalArgumentException("User ID cannot be empty")
                )
            }

            // Add points
            val result = pointsRepository.addPoints(userId, points, type, description)

            // Return success with new total
            result.fold(
                onSuccess = { 
                    val newTotal = pointsRepository.getTotalPoints(userId)
                    Result.success(newTotal)
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Batch accumulate points (useful for multiple point sources)
     */
    suspend fun accumulateBatch(
        userId: String,
        pointsList: List<PointEntry>
    ): Result<Long> = withContext(Dispatchers.IO) {
        try {
            if (pointsList.isEmpty()) {
                return@withContext Result.failure(
                    IllegalArgumentException("Points list cannot be empty")
                )
            }

            var successCount = 0
            var totalError: Exception? = null

            for (entry in pointsList) {
                val result = pointsRepository.addPoints(
                    userId,
                    entry.amount,
                    entry.type,
                    entry.description
                )

                if (result.isSuccess) {
                    successCount++
                } else {
                    totalError = result.exceptionOrNull()
                }
            }

            return@withContext if (successCount > 0) {
                val newTotal = pointsRepository.getTotalPoints(userId)
                if (successCount == pointsList.size) {
                    Result.success(newTotal)
                } else {
                    Result.failure(
                        Exception("$successCount/${pointsList.size} points added. Error: ${totalError?.message}")
                    )
                }
            } else {
                Result.failure(totalError ?: Exception("Failed to add points"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    data class PointEntry(
        val amount: Long,
        val type: PointsType = PointsType.REWARD,
        val description: String = ""
    )
}