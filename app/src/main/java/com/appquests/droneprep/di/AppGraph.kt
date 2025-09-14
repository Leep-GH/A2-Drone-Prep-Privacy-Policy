package com.appquests.droneprep.di

import android.content.Context
import com.appquests.droneprep.data.repo.QuestionRepository
import com.appquests.droneprep.data.prefs.ResultStore
import com.appquests.droneprep.data.prefs.SettingsStore
import com.appquests.droneprep.data.prefs.TopicStatsStore

object AppGraph {
    lateinit var questionRepository: QuestionRepository
        private set
    lateinit var resultStore: ResultStore
        private set
    lateinit var settingsStore: SettingsStore
        private set
    lateinit var topicStatsStore: TopicStatsStore
        private set

    fun init(context: Context) {
        questionRepository = QuestionRepository(context.applicationContext)
        resultStore = ResultStore(context.applicationContext)
        settingsStore = SettingsStore(context.applicationContext)
        topicStatsStore = TopicStatsStore(context.applicationContext)
    }
}
