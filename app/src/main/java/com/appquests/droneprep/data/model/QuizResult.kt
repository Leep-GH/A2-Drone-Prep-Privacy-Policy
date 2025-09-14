package com.appquests.droneprep.data.model

data class QuizResult(
    val id: String,
    val timestamp: Long,
    val mode: QuizMode,
    val topics: List<String>,
    val total: Int,
    val correct: Int,
    val durationSeconds: Int
)

