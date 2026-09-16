package com.example.googleplaypoints.data.repository

import com.example.googleplaypoints.data.local.TopUpDao
import com.example.googleplaypoints.data.local.PointsDao
import com.example.googleplaypoints.data.model.TopUp
import com.example.googleplaypoints.data.model.TopUpStatus
import com.example.googleplaypoints.data.remote.ApiService
import com.example.googleplaypoints.data.remote.TopUpRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TopUpRepository(
    private val topUpDao: TopUpDao,
    private val pointsDao: PointsDao,
    private val apiService: ApiService
) {
    fun getTopUpsByUserId(userId: String): Flow<List<TopUp>> =
        topUpDao.getTopUpsByUserId(userId)
            .catch { e ->
                e.printStackTrace()
                emit(emptyList())
            }

    suspend fun initiateTopUp(
        userId: String,
        packageName: String,
        appName: String,
        amount: Double,
        token: String
    ): Result<TopUp> = withContext(Dispatchers.IO) {
        try {
            if (amount <= 0.0) {
                return@withContext Result.failure(IllegalArgumentException("Amount must be positive"))
            }

            val request = TopUpRequest(packageName, appName, amount, "USD")
            val response = apiService.initiateTopUp(request, "Bearer $token")
            
            val topUp = TopUp(
                userId = userId,
                packageName = packageName,
                appName = appName,
                topUpAmount = amount,
                transactionId = response.transactionId,
                status = TopUpStatus.PENDING
            )
            
            topUpDao.insertTopUp(topUp)
            Result.success(topUp)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun updateTopUpStatus(
        transactionId: String,
        status: TopUpStatus
    ): Result<TopUp> = withContext(Dispatchers.IO) {
        try {
            val topUp = topUpDao.getTopUpByTransactionId(transactionId)
                ?: return@withContext Result.failure(Exception("TopUp not found"))
            
            val updatedTopUp = topUp.copy(status = status)
            topUpDao.updateTopUp(updatedTopUp)
            Result.success(updatedTopUp)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun verifyAndCompleteTopUp(
        transactionId: String,
        token: String
    ): Result<TopUp> = withContext(Dispatchers.IO) {
        try {
            val topUp = topUpDao.getTopUpByTransactionId(transactionId)
                ?: return@withContext Result.failure(Exception("TopUp not found"))

            val verificationResult = apiService.verifyTopUp(transactionId, "Bearer $token")
            
            if (verificationResult["status"] == "completed" || verificationResult["success"] == true) {
                val completedTopUp = topUp.copy(status = TopUpStatus.COMPLETED)
                topUpDao.updateTopUp(completedTopUp)
                Result.success(completedTopUp)
            } else {
                val failedTopUp = topUp.copy(status = TopUpStatus.FAILED)
                topUpDao.updateTopUp(failedTopUp)
                Result.failure(Exception("Verification failed"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val topUp = topUpDao.getTopUpByTransactionId(transactionId)
            if (topUp != null) {
                topUpDao.updateTopUp(topUp.copy(status = TopUpStatus.FAILED))
            }
            Result.failure(e)
        }
    }

    suspend fun getTopUpByTransactionId(transactionId: String): Result<TopUp> = 
        withContext(Dispatchers.IO) {
            try {
                val topUp = topUpDao.getTopUpByTransactionId(transactionId)
                if (topUp != null) {
                    Result.success(topUp)
                } else {
                    Result.failure(Exception("TopUp not found"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }

    suspend fun deleteTopUpsByUserId(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            topUpDao.deleteTopUpsByUserId(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}