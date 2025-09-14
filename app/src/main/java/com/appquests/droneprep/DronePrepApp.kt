package com.appquests.droneprep

import android.app.Application
import android.util.Log
import com.appquests.droneprep.di.AppGraph

class DronePrepApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppGraph.init(this)
        // Debug log to verify loading works (no UI yet)
        runCatching {
            val topics = AppGraph.questionRepository.allTopics()
            val total = AppGraph.questionRepository.totalCount()
            Log.d("DronePrepApp", "Loaded $total questions. Topics: $topics")
        }
    }
}
