package com.example.googleplaypoints.util

import android.util.Log

/**
 * Global exception handler and logging utility
 * Ensures all errors are properly logged and handled
 */
object ErrorHandler {
    private const val TAG = "GooglePlayPoints"

    fun logError(message: String, exception: Exception? = null) {
        Log.e(TAG, message, exception)
    }

    fun logWarning(message: String) {
        Log.w(TAG, message)
    }

    fun logInfo(message: String) {
        Log.i(TAG, message)
    }

    fun logDebug(message: String) {
        Log.d(TAG, message)
    }

    fun handleException(exception: Exception, context: String = "") {
        val fullMessage = if (context.isNotEmpty()) {
            "$context: ${exception.message}"
        } else {
            exception.message ?: "Unknown error"
        }
        logError(fullMessage, exception)
    }
}