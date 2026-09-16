package com.example.googleplaypoints.di

import android.content.Context
import androidx.room.Room
import com.example.googleplaypoints.data.local.AppDatabase
import com.example.googleplaypoints.data.local.PointsDao
import com.example.googleplaypoints.data.local.TopUpDao
import com.example.googleplaypoints.data.local.UserDao
import com.example.googleplaypoints.data.remote.ApiService
import com.example.googleplaypoints.data.repository.AuthRepository
import com.example.googleplaypoints.data.repository.PointsRepository
import com.example.googleplaypoints.data.repository.TopUpRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "google_play_points.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    @Singleton
    fun providePointsDao(database: AppDatabase): PointsDao = database.pointsDao()

    @Provides
    @Singleton
    fun provideTopUpDao(database: AppDatabase): TopUpDao = database.topUpDao()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(okHttpClient: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext context: Context,
        apiService: ApiService,
        userDao: UserDao
    ): AuthRepository {
        return AuthRepository(context, apiService, userDao)
    }

    @Provides
    @Singleton
    fun providePointsRepository(
        pointsDao: PointsDao,
        userDao: UserDao,
        apiService: ApiService
    ): PointsRepository {
        return PointsRepository(pointsDao, userDao, apiService)
    }

    @Provides
    @Singleton
    fun provideTopUpRepository(
        topUpDao: TopUpDao,
        pointsDao: PointsDao,
        apiService: ApiService
    ): TopUpRepository {
        return TopUpRepository(topUpDao, pointsDao, apiService)
    }
}