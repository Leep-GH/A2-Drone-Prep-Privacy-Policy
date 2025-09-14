package com.appquests.droneprep.session

import com.appquests.droneprep.data.model.Question
import com.appquests.droneprep.data.model.Topic
import kotlin.random.Random

object MockSampler {
    
    // Mock exam distribution for 30 questions
    private val mockDistribution = mapOf(
        "Air Law" to 8,
        "Flight Operations" to 8,
        "Human Performance" to 6,
        "Weather" to 4,
        "Safety & Emergencies" to 4
    )
    
    /**
     * Sample questions for mock exam with proper topic distribution
     */
    fun sampleMockQuestions(allQuestions: List<Question>): List<Question> {
        val questionsByTopic = allQuestions.groupBy { it.topic }
        val selectedQuestions = mutableListOf<Question>()
        
        for ((topic, targetCount) in mockDistribution) {
            val topicQuestions = questionsByTopic[topic] ?: emptyList()
            if (topicQuestions.isNotEmpty()) {
                val sampled = topicQuestions.shuffled(Random(System.currentTimeMillis()))
                    .take(targetCount.coerceAtMost(topicQuestions.size))
                selectedQuestions.addAll(sampled)
            }
        }
        
        // If we don't have enough questions, fill with random questions from available pool
        val totalTarget = mockDistribution.values.sum()
        if (selectedQuestions.size < totalTarget) {
            val remaining = allQuestions.filter { it !in selectedQuestions }
                .shuffled(Random(System.currentTimeMillis()))
                .take(totalTarget - selectedQuestions.size)
            selectedQuestions.addAll(remaining)
        }
        
        return selectedQuestions.shuffled(Random(System.currentTimeMillis()))
    }
    
    /**
     * Get the target distribution for mock exams
     */
    fun getMockDistribution(): Map<String, Int> = mockDistribution
}