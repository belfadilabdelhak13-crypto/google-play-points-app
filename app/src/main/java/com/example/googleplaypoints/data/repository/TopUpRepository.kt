package com.example.googleplaypoints.data.repository

import com.example.googleplaypoints.data.local.TopUpDao
import com.example.googleplaypoints.data.model.TopUp
import com.example.googleplaypoints.data.model.TopUpStatus
import com.example.googleplaypoints.data.remote.ApiService
import com.example.googleplaypoints.data.remote.TopUpRequest
import kotlinx.coroutines.flow.Flow

class TopUpRepository(
    private val topUpDao: TopUpDao,
    private val apiService: ApiService
) {
    fun getTopUpsByUserId(userId: String): Flow<List<TopUp>> =
        topUpDao.getTopUpsByUserId(userId)

    suspend fun initiateTopUp(
        userId: String,
        packageName: String,
        appName: String,
        amount: Double,
        token: String
    ): Result<TopUp> = runCatching {
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
        topUp
    }

    suspend fun updateTopUpStatus(transactionId: String, status: TopUpStatus) {
        val topUp = topUpDao.getTopUpByTransactionId(transactionId)
        topUp?.let {
            topUpDao.updateTopUp(it.copy(status = status))
        }
    }
}