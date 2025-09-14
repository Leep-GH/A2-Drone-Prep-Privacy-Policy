package com.appquests.droneprep.data.model

/**
 * Represents an aviation acronym with its definition
 */
data class Acronym(
    val acronym: String,
    val definition: String
)

/**
 * Container for the JSON structure of the acronyms file
 */
data class AcronymData(
    val acronyms: List<Acronym>
)

