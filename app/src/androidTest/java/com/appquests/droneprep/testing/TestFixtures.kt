package com.appquests.droneprep.testing

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.appquests.droneprep.data.model.Question
import com.appquests.droneprep.data.model.QuizMode
import com.appquests.droneprep.data.model.QuizResult
import com.appquests.droneprep.data.model.Topic

/**
 * Test utilities and fixtures for consistent testing
 */
object TestFixtures {
    
    val sampleQuestions = listOf(
        Question(
            id = "TEST-001",
            topic = "Air Law", 
            stem = "What is the minimum age for A2 CofC?",
            options = listOf("16 years", "18 years", "21 years", "No minimum"),
            correctIndex = 0,
            explanation = "A2 CofC minimum age is 16 years"
        ),
        Question(
            id = "TEST-002",
            topic = "Operations",
            stem = "Maximum height in Open category?", 
            options = listOf("120m", "150m", "200m", "No limit"),
            correctIndex = 0,
            explanation = "Maximum height is 120m in Open category"
        ),
        Question(
            id = "TEST-003",
            topic = "Weather",
            stem = "What indicates strong winds?",
            options = listOf("Clear skies", "High clouds", "Wind sock horizontal", "Low temperature"),
            correctIndex = 2,
            explanation = "Horizontal wind sock indicates strong winds"
        )
    )
    
    val sampleQuizResult = QuizResult(
        id = "test-result-001",
        mode = QuizMode.PRACTICE,
        timestamp = System.currentTimeMillis(),
        topics = listOf("Air Law", "Operations"),
        correct = 8,
        total = 10
    )
    
    val sampleTopics = Topic.values().map { it.displayName }
}

/**
 * Test extensions for Compose testing
 */
fun ComposeContentTestRule.assertTextContains(text: String) {
    onNodeWithText(text, substring = true).assertIsDisplayed()
}

fun ComposeContentTestRule.assertContentDescription(description: String) {
    onNodeWithContentDescription(description).assertIsDisplayed()
}

fun ComposeContentTestRule.clickByContentDescription(description: String) {
    onNodeWithContentDescription(description).performClick()
}

fun ComposeContentTestRule.assertButtonEnabled(text: String) {
    onNodeWithText(text).assertIsEnabled()
}

fun ComposeContentTestRule.assertButtonDisabled(text: String) {
    onNodeWithText(text).assertIsNotEnabled()
}

/**
 * Helper for testing quiz scenarios
 */
class QuizTestHelper(private val composeRule: ComposeContentTestRule) {
    
    fun selectAnswer(optionIndex: Int) {
        composeRule.onAllNodesWithTag("answer_option")[optionIndex].performClick()
    }
    
    fun clickNext() {
        composeRule.onNodeWithText("Next").performClick()
    }
    
    fun clickSubmit() {
        composeRule.onNodeWithText("Submit").performClick()
    }
    
    fun toggleFlag() {
        composeRule.onNodeWithContentDescription("Flag question").performClick()
    }
    
    fun assertQuestionDisplayed(questionNumber: Int, totalQuestions: Int) {
        composeRule.onNodeWithText("$questionNumber of $totalQuestions").assertIsDisplayed()
    }
    
    fun assertScoreDisplayed(correct: Int, total: Int) {
        composeRule.onNodeWithText("$correct/$total correct").assertIsDisplayed()
    }
}

/**
 * Creates a QuizTestHelper for the current compose rule
 */
fun ComposeContentTestRule.quizTestHelper(): QuizTestHelper {
    return QuizTestHelper(this)
}
