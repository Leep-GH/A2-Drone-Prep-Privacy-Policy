package com.appquests.droneprep.session

import com.appquests.droneprep.data.model.Question
import com.appquests.droneprep.data.model.QuizMode
import com.appquests.droneprep.data.model.QuizResult

// Add this data class near the top-level (in the same file)
data class TopicStat(val topic: String, val correct: Int, val total: Int) {
    val percent: Int get() = if (total == 0) 0 else (correct * 100 / total)
}

private fun shuffledCopy(q: Question): Question {
    // Shuffle options but compute new correct index
    val indexed = q.options.mapIndexed { i, s -> i to s }.shuffled()
    val newOptions = indexed.map { it.second }
    val newCorrectIdx = indexed.indexOfFirst { it.first == q.correctIndex }.coerceAtLeast(0)
    return q.copy(options = newOptions, correctIndex = newCorrectIdx)
}

object QuizSessionHolder {
    var questions: List<Question> = emptyList()
        private set
    var mode: QuizMode = QuizMode.PRACTICE
        private set

    private var startTime: Long = 0L
    private var answers: MutableList<Int?> = mutableListOf()
    private var flags: MutableSet<Int> = mutableSetOf()
    private var reviewSubset: List<Int>? = null // null = review all

    // Expose last built result so ResultsScreen can read it
    var lastResult: QuizResult? = null
        private set

    fun startSession(qs: List<Question>, m: QuizMode) {
        questions = qs.map { shuffledCopy(it) }           // shuffled copy
        mode = m
        startTime = System.currentTimeMillis()
        answers = MutableList(questions.size) { null }
        flags.clear()
        reviewSubset = null
        lastResult = null
    }

    fun recordAnswer(index: Int, selection: Int) {
        if (index in answers.indices) answers[index] = selection
    }

    fun buildResult(): QuizResult {
        val correctCount = questions.indices.count { i ->
            val sel = answers[i]
            sel != null && sel == questions[i].correctIndex
        }
        val duration = ((System.currentTimeMillis() - startTime) / 1000).toInt()
        val result = QuizResult(
            id = System.currentTimeMillis().toString(),
            timestamp = System.currentTimeMillis(),
            mode = mode,
            topics = questions.map { it.topic }.distinct(),
            total = questions.size,
            correct = correctCount,
            durationSeconds = duration
        )
        lastResult = result
        return result
    }

    fun clear() {
        questions = emptyList()
        answers.clear()
        startTime = 0L
        flags.clear()
        reviewSubset = null
        lastResult = null
    }

    fun topicBreakdown(): List<TopicStat> {
        if (questions.isEmpty()) return emptyList()
        // We need the recorded answers; rebuild correctness by comparing to answers list length
        // answers is internal; expose counts by computing on the fly:
        val stats = linkedMapOf<String, Pair<Int, Int>>() // topic -> (correct, total)
        questions.forEachIndexed { i, q ->
            val pair = stats[q.topic] ?: (0 to 0)
            val isCorrect = runCatching {
                // answers[i] may be null if user never selected (edge case)
                val sel = try {
                    // reflectively read our internal list safely
                    // If access is restricted, assume null; but we have access here.
                    // We'll compute using a safe path:
                    // (This block is inside the same object; we can access answers directly.)
                    answers.getOrNull(i)
                } catch (_: Exception) { null }
                sel != null && sel == q.correctIndex
            }.getOrDefault(false)
            val newCorrect = pair.first + if (isCorrect) 1 else 0
            val newTotal = pair.second + 1
            stats[q.topic] = newCorrect to newTotal
        }
        return stats.map { (topic, ct) -> TopicStat(topic, ct.first, ct.second) }
    }

    fun weakestTopics(n: Int = 2): List<String> {
        val br = topicBreakdown()
        if (br.isEmpty()) return emptyList()
        return br.sortedWith(
            compareBy<TopicStat> { it.percent }.thenBy { it.total }
        ).map { it.topic }.take(n)
    }

    fun elapsedSeconds(): Int =
        if (startTime > 0L) ((System.currentTimeMillis() - startTime) / 1000).toInt() else 0

    fun answersSnapshot(): List<Int?> = answers.toList()

    fun toggleFlag(index: Int) {
        if (index in questions.indices) {
            if (!flags.add(index)) flags.remove(index)
        }
    }

    fun isFlagged(index: Int): Boolean = index in flags

    fun flaggedCount(): Int = flags.size

    fun startFlaggedReview() {
        reviewSubset = flags.toList().sorted()
    }

    fun clearReviewSubset() {
        reviewSubset = null
    }

    fun reviewQuestions(): List<Question> {
        return reviewSubset?.mapNotNull { index ->
            questions.getOrNull(index)
        } ?: questions
    }

    fun reviewAnswers(): List<Int?> {
        return reviewSubset?.mapNotNull { index ->
            answers.getOrNull(index)
        } ?: answers
    }


}
