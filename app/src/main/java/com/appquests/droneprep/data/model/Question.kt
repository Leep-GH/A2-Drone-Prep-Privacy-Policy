package com.appquests.droneprep.data.model

/**
 * Represents a quiz question with multiple choice options and additional learning materials.
 *
 * @property id Unique identifier for the question
 * @property topic The topic category this question belongs to
 * @property stem The main question text
 * @property options List of possible answers (typically 4 options)
 * @property correctIndex Zero-based index of the correct answer in options
 * @property explanation Primary explanation of why the answer is correct
 * @property more Optional additional detailed information
 * @property ref Optional reference source or regulation number
 * @property tip Optional helpful tip or memory aid
 */
data class Question(
    val id: String,
    val topic: String,
    val stem: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val more: String? = null,
    val ref: String? = null,
    val tip: String? = null
)
