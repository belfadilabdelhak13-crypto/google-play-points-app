package com.example.googleplaypoints.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topups")
data class TopUp(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val packageName: String,
    val appName: String,
    val topUpAmount: Double,
    val currency: String = "USD",
    val transactionId: String,
    val status: TopUpStatus = TopUpStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis()
)

enum class TopUpStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED
}