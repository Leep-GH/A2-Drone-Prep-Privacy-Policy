package com.appquests.droneprep.session

import com.appquests.droneprep.data.model.QuizMode
import com.appquests.droneprep.data.model.QuizResult
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * Unit tests for QuizSessionHolder business logic.
 */
class QuizSessionHolderTest {
    
    @Before
    fun setUp() {
        // Reset session state before each test
        QuizSessionHolder.reset()
    }
    
    @Test
    fun `test session initialization with practice mode`() {
        val topics = listOf("Air Law", "Operations")
        QuizSessionHolder.startSession(topics, 10, QuizMode.PRACTICE)
        
        assertEquals(10, QuizSessionHolder.totalQuestions())
        assertEquals(0, QuizSessionHolder.currentIndex())
        assertFalse(QuizSessionHolder.isComplete())
    }
    
    @Test
    fun `test session initialization with mock mode`() {
        val topics = listOf("Air Law", "Operations", "Weather")
        QuizSessionHolder.startSession(topics, 30, QuizMode.MOCK)
        
        assertEquals(30, QuizSessionHolder.totalQuestions())
        assertEquals(QuizMode.MOCK, QuizSessionHolder.getMode())
    }
    
    @Test
    fun `test answer submission updates progress`() {
        val topics = listOf("Air Law")
        QuizSessionHolder.startSession(topics, 5, QuizMode.PRACTICE)
        
        val initialIndex = QuizSessionHolder.currentIndex()
        QuizSessionHolder.submitAnswer(1) // Submit answer for option index 1
        
        assertEquals(initialIndex + 1, QuizSessionHolder.currentIndex())
    }
    
    @Test
    fun `test session completion`() {
        val topics = listOf("Air Law")
        QuizSessionHolder.startSession(topics, 2, QuizMode.PRACTICE)
        
        // Answer all questions
        QuizSessionHolder.submitAnswer(0)
        QuizSessionHolder.submitAnswer(1)
        
        assertTrue(QuizSessionHolder.isComplete())
    }
    
    @Test
    fun `test quiz result generation`() {
        val topics = listOf("Air Law", "Operations")
        QuizSessionHolder.startSession(topics, 3, QuizMode.MOCK)
        
        // Simulate answering questions
        QuizSessionHolder.submitAnswer(0) // Assume correct
        QuizSessionHolder.submitAnswer(1) // Assume incorrect  
        QuizSessionHolder.submitAnswer(0) // Assume correct
        
        val result = QuizSessionHolder.buildResult()
        
        assertNotNull(result)
        assertEquals(3, result.total)
        assertEquals(QuizMode.MOCK, result.mode)
        assertEquals(topics, result.topics)
        assertTrue(result.timestamp > 0)
    }
    
    @Test
    fun `test flagged questions tracking`() {
        val topics = listOf("Air Law")
        QuizSessionHolder.startSession(topics, 3, QuizMode.PRACTICE)
        
        // Flag first question
        QuizSessionHolder.toggleFlag()
        assertEquals(1, QuizSessionHolder.flaggedCount())
        
        // Unflag it
        QuizSessionHolder.toggleFlag()
        assertEquals(0, QuizSessionHolder.flaggedCount())
    }
    
    @Test
    fun `test weakest topics calculation`() {
        // This would require mock data or dependency injection
        // For now, test that the method doesn't crash
        val weakTopics = QuizSessionHolder.weakestTopics(2)
        assertNotNull(weakTopics)
        assertTrue(weakTopics.size <= 2)
    }
    
    @Test
    fun `test topic breakdown generation`() {
        val breakdown = QuizSessionHolder.topicBreakdown()
        assertNotNull(breakdown)
        // Additional assertions would depend on mock data setup
    }
}
