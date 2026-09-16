package com.example.googleplaypoints.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Header

data class LoginRequest(val googleIdToken: String)
data class LoginResponse(val userId: String, val token: String, val user: Map<String, Any>)

data class PointsResponse(val totalPoints: Long, val recentTransactions: List<Map<String, Any>>)

data class TopUpRequest(
    val packageName: String,
    val appName: String,
    val amount: Double,
    val currency: String
)

data class TopUpResponse(
    val transactionId: String,
    val status: String,
    val message: String
)

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): Map<String, Any>

    @GET("users/{userId}/points")
    suspend fun getUserPoints(
        @Path("userId") userId: String,
        @Header("Authorization") token: String
    ): PointsResponse

    @POST("topup/initiate")
    suspend fun initiateTopUp(
        @Body request: TopUpRequest,
        @Header("Authorization") token: String
    ): TopUpResponse

    @POST("topup/verify/{transactionId}")
    suspend fun verifyTopUp(
        @Path("transactionId") transactionId: String,
        @Header("Authorization") token: String
    ): Map<String, Any>
}