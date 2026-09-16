package com.example.googleplaypoints.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.googleplaypoints.data.model.TopUp
import kotlinx.coroutines.flow.Flow

@Dao
interface TopUpDao {
    @Insert
    suspend fun insertTopUp(topUp: TopUp)

    @Query("SELECT * FROM topups WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTopUpsByUserId(userId: String): Flow<List<TopUp>>

    @Query("SELECT * FROM topups WHERE transactionId = :transactionId")
    suspend fun getTopUpByTransactionId(transactionId: String): TopUp?

    @Update
    suspend fun updateTopUp(topUp: TopUp)

    @Query("DELETE FROM topups WHERE userId = :userId")
    suspend fun deleteTopUpsByUserId(userId: String)
}