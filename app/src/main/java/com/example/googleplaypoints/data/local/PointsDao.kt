package com.example.googleplaypoints.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.googleplaypoints.data.model.Points
import kotlinx.coroutines.flow.Flow

@Dao
interface PointsDao {
    @Insert
    suspend fun insertPoints(points: Points)

    @Query("SELECT * FROM points WHERE userId = :userId ORDER BY timestamp DESC")
    fun getPointsByUserId(userId: String): Flow<List<Points>>

    @Query("SELECT SUM(pointsAmount) FROM points WHERE userId = :userId")
    suspend fun getTotalPoints(userId: String): Long?

    @Query("SELECT * FROM points WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentPoints(userId: String, limit: Int): List<Points>

    @Query("DELETE FROM points WHERE userId = :userId")
    suspend fun deletePointsByUserId(userId: String)
}