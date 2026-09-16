package com.example.googleplaypoints.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "points")
data class Points(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val pointsAmount: Long,
    val pointsType: PointsType,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class PointsType {
    PURCHASE,
    REWARD,
    REFERRAL,
    DAILY_LOGIN,
    ACHIEVEMENT,
    PROMOTION
}