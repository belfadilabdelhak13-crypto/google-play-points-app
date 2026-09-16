package com.example.googleplaypoints.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.googleplaypoints.data.model.Points
import com.example.googleplaypoints.data.model.TopUp
import com.example.googleplaypoints.data.model.User

@Database(
    entities = [User::class, Points::class, TopUp::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun pointsDao(): PointsDao
    abstract fun topUpDao(): TopUpDao
}