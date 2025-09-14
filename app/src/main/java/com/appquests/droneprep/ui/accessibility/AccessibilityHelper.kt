package com.appquests.droneprep.ui.accessibility

import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlin.math.pow

/**
 * Accessibility utilities and constants for the app
 */
object AccessibilityHelper {
    
    // Minimum touch target size for accessibility
    val MinTouchTarget = 48.dp
    
    // Content descriptions for common UI elements
    const val QUIZ_PROGRESS = "Quiz progress"
    const val FLAG_QUESTION = "Flag this question"
    const val UNFLAG_QUESTION = "Unflag this question"
    const val NEXT_QUESTION = "Go to next question"
    const val PREVIOUS_QUESTION = "Go to previous question"
    const val SUBMIT_QUIZ = "Submit quiz"
    const val ANSWER_OPTION = "Answer option"
    const val CORRECT_ANSWER = "Correct answer"
    const val INCORRECT_ANSWER = "Incorrect answer"
    const val SELECTED_ANSWER = "Selected answer"
    
    /**
     * Checks if color contrast meets WCAG AA standards (4.5:1 for normal text)
     */
    fun hasGoodContrast(foreground: Color, background: Color): Boolean {
        val contrastRatio = calculateContrastRatio(foreground, background)
        return contrastRatio >= 4.5f
    }
    
    /**
     * Calculates the contrast ratio between two colors
     */
    private fun calculateContrastRatio(color1: Color, color2: Color): Float {
        val lum1 = relativeLuminance(color1)
        val lum2 = relativeLuminance(color2)
        val brighter = maxOf(lum1, lum2)
        val darker = minOf(lum1, lum2)
        return (brighter + 0.05f) / (darker + 0.05f)
    }
    
    /**
     * Calculates relative luminance of a color
     */
    private fun relativeLuminance(color: Color): Float {
        val r = if (color.red <= 0.03928f) color.red / 12.92f else 
            ((color.red + 0.055f) / 1.055f).toDouble().pow(2.4).toFloat()
        val g = if (color.green <= 0.03928f) color.green / 12.92f else 
            ((color.green + 0.055f) / 1.055f).toDouble().pow(2.4).toFloat()
        val b = if (color.blue <= 0.03928f) color.blue / 12.92f else 
            ((color.blue + 0.055f) / 1.055f).toDouble().pow(2.4).toFloat()
        
        return 0.2126f * r + 0.7152f * g + 0.0722f * b
    }
}

/**
 * Modifier extension for adding accessibility content descriptions
 */
fun Modifier.accessibilityDescription(description: String): Modifier {
    return this.semantics { contentDescription = description }
}

/**
 * Ensures minimum touch target size for accessibility
 */
fun Modifier.accessibleTouchTarget(): Modifier {
    return this.size(AccessibilityHelper.MinTouchTarget)
}
