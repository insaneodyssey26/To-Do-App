package com.masum.todo.utils

import android.util.Log

object ErrorHandler {
    private const val TAG = "TodoApp"
    
    fun logError(message: String, throwable: Throwable? = null) {
        Log.e(TAG, message, throwable)
    }
    
    fun getErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is java.net.UnknownHostException -> "No internet connection"
            is java.util.concurrent.TimeoutException -> "Request timeout"
            is IllegalArgumentException -> "Invalid input: ${throwable.message}"
            else -> throwable.message ?: "An unexpected error occurred"
        }
    }
}
