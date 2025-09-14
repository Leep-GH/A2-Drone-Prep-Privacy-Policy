package com.appquests.droneprep.util

import androidx.compose.runtime.*

/**
 * Performance utilities for Compose optimization
 */
object PerformanceUtils {
    
    /**
     * Stable wrapper for expensive calculations that should only recompute when dependencies change
     */
    @Stable
    data class StableHolder<T>(val value: T)
    
    /**
     * Creates a stable wrapper for expensive repository calls
     */
    @Composable
    fun <T> rememberStable(calculation: () -> T): StableHolder<T> {
        return remember { StableHolder(calculation()) }
    }
    
    /**
     * Optimized remember for repository data that rarely changes
     */
    @Composable
    fun <T> rememberRepositoryData(key: Any?, calculation: () -> T): T {
        return remember(key) { calculation() }
    }
}

/**
 * Extension function for better performance when accessing AppGraph singletons
 */
@Composable
inline fun <T> rememberAppGraphValue(crossinline accessor: () -> T): T {
    return remember { accessor() }
}
