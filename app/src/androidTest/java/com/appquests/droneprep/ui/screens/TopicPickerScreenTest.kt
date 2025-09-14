package com.appquests.droneprep.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.appquests.droneprep.ui.theme.A2CofCDronePrepTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for the TopicPickerScreen composable.
 * These tests verify that the UI behaves correctly for user interactions.
 */
@RunWith(AndroidJUnit4::class)
class TopicPickerScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun topicPickerScreen_displaysAllTopics() {
        val testTopics = listOf(
            TopicUi("airlaw", "Air Law", "Test description", androidx.compose.material.icons.Icons.Rounded.Gavel),
            TopicUi("ops", "Operations", "Test description", androidx.compose.material.icons.Icons.Rounded.Settings)
        )

        composeTestRule.setContent {
            A2CofCDronePrepTheme {
                TopicPickerScreen(
                    topics = testTopics,
                    selectedIds = emptySet(),
                    onCancel = {},
                    onDone = {}
                )
            }
        }

        // Verify the screen title is displayed
        composeTestRule.onNodeWithText("Choose Topics").assertIsDisplayed()
        
        // Verify all topics are displayed
        composeTestRule.onNodeWithText("Air Law").assertIsDisplayed()
        composeTestRule.onNodeWithText("Operations").assertIsDisplayed()
        
        // Verify action buttons are present
        composeTestRule.onNodeWithText("Select all").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clear").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
        composeTestRule.onNodeWithText("Done").assertIsDisplayed()
    }

    @Test
    fun topicPickerScreen_selectAllWorksCorrectly() {
        val testTopics = listOf(
            TopicUi("airlaw", "Air Law", "Test description", androidx.compose.material.icons.Icons.Rounded.Gavel),
            TopicUi("ops", "Operations", "Test description", androidx.compose.material.icons.Icons.Rounded.Settings)
        )

        composeTestRule.setContent {
            A2CofCDronePrepTheme {
                TopicPickerScreen(
                    topics = testTopics,
                    selectedIds = emptySet(),
                    onCancel = {},
                    onDone = {}
                )
            }
        }

        // Initially, should show "0 selected"
        composeTestRule.onNodeWithText("0 selected").assertIsDisplayed()
        
        // Click "Select all"
        composeTestRule.onNodeWithText("Select all").performClick()
        
        // Should now show "2 selected"
        composeTestRule.onNodeWithText("2 selected").assertIsDisplayed()
    }

    @Test
    fun topicPickerScreen_clearWorksCorrectly() {
        val testTopics = listOf(
            TopicUi("airlaw", "Air Law", "Test description", androidx.compose.material.icons.Icons.Rounded.Gavel)
        )

        composeTestRule.setContent {
            A2CofCDronePrepTheme {
                TopicPickerScreen(
                    topics = testTopics,
                    selectedIds = setOf("airlaw"), // Start with one selected
                    onCancel = {},
                    onDone = {}
                )
            }
        }

        // Should show "1 selected"
        composeTestRule.onNodeWithText("1 selected").assertIsDisplayed()
        
        // Click "Clear"
        composeTestRule.onNodeWithText("Clear").performClick()
        
        // Should now show "0 selected"
        composeTestRule.onNodeWithText("0 selected").assertIsDisplayed()
    }

    @Test
    fun topicPickerScreen_individualTopicSelection() {
        val testTopics = listOf(
            TopicUi("airlaw", "Air Law", "Test description", androidx.compose.material.icons.Icons.Rounded.Gavel)
        )

        composeTestRule.setContent {
            A2CofCDronePrepTheme {
                TopicPickerScreen(
                    topics = testTopics,
                    selectedIds = emptySet(),
                    onCancel = {},
                    onDone = {}
                )
            }
        }

        // Click on the Air Law topic
        composeTestRule.onNodeWithText("Air Law").performClick()
        
        // Should show "1 selected"
        composeTestRule.onNodeWithText("1 selected").assertIsDisplayed()
    }
}
