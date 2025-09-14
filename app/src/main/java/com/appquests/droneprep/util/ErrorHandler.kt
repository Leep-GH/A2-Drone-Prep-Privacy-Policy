package com.appquests.droneprep.util

import android.content.Context
import android.util.Log
import com.appquests.droneprep.R

/**
 * Utility object for handling errors and logging throughout the app.
 */
object ErrorHandler {
    private const val TAG = "DronePrepApp"
    
    /**
     * Logs an error and returns a user-friendly message.
     */
    fun handleError(
        context: Context,
        throwable: Throwable,
        operation: String = "operation"
    ): String {
        Log.e(TAG, "Error during $operation", throwable)
        
        return when (throwable) {
            is java.io.IOException -> context.getString(R.string.error_loading_questions)
            is IllegalArgumentException -> context.getString(R.string.error_no_questions)
            else -> "An unexpected error occurred during $operation"
        }
    }
    
    /**
     * Logs an error with a custom message.
     */
    fun logError(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(TAG, message, throwable)
        } else {
            Log.e(TAG, message)
        }
    }
    
    /**
     * Logs a warning.
     */
    fun logWarning(message: String) {
        Log.w(TAG, message)
    }
    
    /**
     * Logs info for debugging.
     */
    fun logInfo(message: String) {
        Log.i(TAG, message)
    }
}
