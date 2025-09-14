package com.appquests.droneprep.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.appquests.droneprep.session.TopicStat
import kotlin.math.max

data class TopicTotals(val correct: Int, val total: Int) {
    fun mergedWith(other: TopicTotals) =
        TopicTotals(correct + other.correct, total + other.total)
    val percent: Int get() = if (total == 0) 0 else (correct * 100 / total)
}

class TopicStatsStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("topic_stats_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val key = "topic_stats_json"

    fun getAll(): Map<String, TopicTotals> {
        val json = prefs.getString(key, null) ?: return emptyMap()
        val type = object : TypeToken<Map<String, TopicTotals>>() {}.type
        return runCatching { gson.fromJson<Map<String, TopicTotals>>(json, type) }.getOrElse { emptyMap() }
    }

    private fun saveAll(map: Map<String, TopicTotals>) {
        prefs.edit().putString(key, gson.toJson(map)).apply()
    }

    /** Merge a single session breakdown into totals */
    fun applyBreakdown(breakdown: List<TopicStat>) {
        if (breakdown.isEmpty()) return
        val current = getAll().toMutableMap()
        breakdown.forEach { stat ->
            val existing = current[stat.topic]
            val add = TopicTotals(stat.correct, stat.total)
            current[stat.topic] = if (existing == null) add else existing.mergedWith(add)
        }
        saveAll(current)
    }

    /** Return up to n weakest topics that have at least 1 question answered */
    fun weakestTopics(n: Int = 2): List<String> {
        val all = getAll()
        if (all.isEmpty()) return emptyList()
        return all.entries
            .filter { it.value.total > 0 }
            .sortedWith(compareBy<Map.Entry<String, TopicTotals>> { it.value.percent }.thenBy { it.value.total })
            .map { it.key }
            .take(n)
    }

    fun clear() {
        prefs.edit().remove(key).apply()
    }
}

