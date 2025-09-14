package com.appquests.droneprep.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SettingsStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    // Existing
    private val KEY_TIMER = "mock_timer_enabled"

    // Existing keys you added previously
    private val KEY_LAST_MODE = "last_mode"
    private val KEY_LAST_COUNT = "last_count"
    private val KEY_LAST_TOPICS = "last_topics_json"

    // NEW: first-run disclaimer flag
    private val KEY_ONBOARD_SEEN = "onboard_seen_v1"

    private val gson = Gson()

    // --- Existing API ---
    fun isMockTimerEnabled(): Boolean =
        prefs.getBoolean(KEY_TIMER, true) // default ON
    fun setMockTimerEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_TIMER, enabled).apply()
    }

    // --- NEW API ---
    fun getLastMode(): String = prefs.getString(KEY_LAST_MODE, "PRACTICE") ?: "PRACTICE"
    fun setLastMode(value: String) {
        prefs.edit().putString(KEY_LAST_MODE, value).apply()
    }

    fun getLastCount(): Int = prefs.getInt(KEY_LAST_COUNT, 20)
    fun setLastCount(value: Int) {
        prefs.edit().putInt(KEY_LAST_COUNT, value).apply()
    }

    fun getLastTopics(): List<String> {
        val json = prefs.getString(KEY_LAST_TOPICS, null) ?: return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return runCatching { gson.fromJson<List<String>>(json, type) }.getOrElse { emptyList() }
    }
    fun setLastTopics(topics: List<String>) {
        prefs.edit().putString(KEY_LAST_TOPICS, gson.toJson(topics)).apply()
    }

    // --- NEW API for first-run dialog ---
    fun hasSeenOnboarding(): Boolean = prefs.getBoolean(KEY_ONBOARD_SEEN, false)
    fun setOnboardingSeen() { prefs.edit().putBoolean(KEY_ONBOARD_SEEN, true).apply() }
}
