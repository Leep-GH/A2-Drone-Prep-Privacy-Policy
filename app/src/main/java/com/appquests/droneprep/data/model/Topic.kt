package com.appquests.droneprep.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Enum representing the different aviation topics available in the app.
 * Each topic has a display name for UI, a key for data storage, and an icon.
 *
 * @property displayName Human-readable name shown in the UI
 * @property key Short identifier used for data storage and API calls
 * @property icon Material icon representing the topic
 */
enum class Topic(
    val displayName: String,
    val key: String,
    val icon: ImageVector
) {
    AIR_LAW("Air Law", "airlaw", Icons.Filled.MenuBook),
    FLIGHT_OPERATIONS("Flight Operations", "operations", Icons.Filled.FlashOn),
    HUMAN_PERFORMANCE("Human Performance", "human", Icons.Filled.CenterFocusStrong),
    WEATHER("Weather", "weather", Icons.Filled.History),
    SAFETY_EMERGENCIES("Safety & Emergencies", "safety", Icons.Filled.Warning);

    companion object {
        /**
         * Finds a topic by its display name.
         * @param displayName The display name to search for
         * @return The matching topic or null if not found
         */
        fun fromDisplayName(displayName: String): Topic? {
            return values().find { it.displayName == displayName }
        }
        
        /**
         * Finds a topic by its key identifier.
         * @param key The key to search for
         * @return The matching topic or null if not found
         */
        fun fromKey(key: String): Topic? {
            return values().find { it.key == key }
        }
    }
}