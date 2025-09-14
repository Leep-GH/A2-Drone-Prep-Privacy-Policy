package com.appquests.droneprep.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.appquests.droneprep.data.model.QuizResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ResultStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("results_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val key = "results_history_json"

    fun getResults(): List<QuizResult> {
        val json = prefs.getString(key, null) ?: return emptyList()
        val type = object : TypeToken<List<QuizResult>>() {}.type
        return runCatching { gson.fromJson<List<QuizResult>>(json, type) }.getOrElse { emptyList() }
    }

    fun saveResult(result: QuizResult, keep: Int = 10) {
        val current = getResults().toMutableList()
        current.add(0, result)
        while (current.size > keep) current.removeLast()
        prefs.edit().putString(key, gson.toJson(current)).apply()
    }

    fun clear() {
        prefs.edit().remove(key).apply()
    }
}
