package com.example.googleplaypoints.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.googleplaypoints.data.local.UserDao
import com.example.googleplaypoints.data.model.User
import com.example.googleplaypoints.data.remote.ApiService
import com.example.googleplaypoints.data.remote.LoginRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class AuthRepository(
    private val context: Context,
    private val apiService: ApiService,
    private val userDao: UserDao
) {
    private val USER_ID_KEY = stringPreferencesKey("user_id")
    private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")

    val userIdFlow: Flow<String?> = context.dataStore.data.map { it[USER_ID_KEY] }
    val authTokenFlow: Flow<String?> = context.dataStore.data.map { it[AUTH_TOKEN_KEY] }

    suspend fun loginWithGoogle(googleIdToken: String): Result<User> = runCatching {
        val response = apiService.login(LoginRequest(googleIdToken))
        val user = User(
            userId = response.userId,
            email = response.user["email"] as? String ?: "",
            displayName = response.user["displayName"] as? String ?: "",
            profileImageUrl = response.user["profileImage"] as? String
        )
        userDao.insertUser(user)
        saveAuthToken(response.userId, response.token)
        user
    }

    private suspend fun saveAuthToken(userId: String, token: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[AUTH_TOKEN_KEY] = token
        }
    }

    suspend fun logout() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun getCurrentUserId(): String? =
        context.dataStore.data.map { it[USER_ID_KEY] }.collect { return it }
}