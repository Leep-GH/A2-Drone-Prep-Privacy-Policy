package com.appquests.droneprep.data.repo

import android.content.Context
import com.appquests.droneprep.data.model.Question
import com.appquests.droneprep.data.model.Topic
import com.appquests.droneprep.data.model.Acronym
import com.appquests.droneprep.data.model.AcronymData
import com.appquests.droneprep.util.ErrorHandler
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random

/**
 * Repository for managing question data with improved performance and error handling
 */
class QuestionRepository(private val context: Context) {

    @Volatile private var cache: List<Question>? = null
    @Volatile private var acronymCache: List<Acronym>? = null
    
    // Reuse Gson instance for better performance
    private val gson = Gson()
    
    // Precompute type for better performance
    private val listType = object : TypeToken<List<Question>>() {}.type
    private val acronymDataType = object : TypeToken<AcronymData>() {}.type

    private val topicFileMap = mapOf(
        "Air Law" to "questions_air_law.json",
        "Flight Operations" to "questions_flight_operations.json", 
        "Human Performance" to "questions_human_performance.json",
        "Weather" to "questions_weather.json",
        "Safety & Emergencies" to "questions_safety_emergencies.json"
    )

    /**
     * Loads all questions from assets. Should be called from a background thread.
     */
    suspend fun loadAll(): List<Question> = withContext(Dispatchers.IO) {
        cache?.let { return@withContext it }
        
        val allQuestions = mutableListOf<Question>()
        
        // Load questions from each topic file
        topicFileMap.forEach { (topicName, fileName) ->
            try {
                loadQuestionsFromFile(fileName, allQuestions)
            } catch (e: Exception) {
                ErrorHandler.logError("Failed to load questions from $fileName for topic $topicName", e)
            }
        }
        
        cache = allQuestions
        allQuestions
    }
    
    /**
     * Helper method to load questions from a specific file
     */
    private fun loadQuestionsFromFile(fileName: String, destination: MutableList<Question>) {
        val input = context.assets.open(fileName)
        val reader = BufferedReader(InputStreamReader(input))
        val json = reader.use { it.readText() }
        val items: List<Question> = gson.fromJson(json, listType)
        destination.addAll(items)
    }
    
    /**
     * Gets all questions. Use from main thread only if cache is available.
     */
    private fun loadAllSync(): List<Question> {
        cache?.let { return it }
        
        val allQuestions = mutableListOf<Question>()
        
        // Load questions from each topic file
        topicFileMap.forEach { (topicName, fileName) ->
            try {
                loadQuestionsFromFile(fileName, allQuestions)
            } catch (e: Exception) {
                ErrorHandler.logError("Failed to load questions from $fileName for topic $topicName", e)
            }
        }
        
        cache = allQuestions
        return allQuestions
    }

    fun allTopics(): List<String> =
        loadAllSync().map { it.topic }.distinct().sorted()

    fun getRandomQuestions(topics: List<String>, count: Int): List<Question> {
        val pool = loadAllSync().filter { topics.isEmpty() || it.topic in topics }
        if (pool.isEmpty()) return emptyList()
        return pool.shuffled(Random(System.currentTimeMillis()))
            .take(count.coerceAtMost(pool.size))
    }
    
    fun getMockExamQuestions(): List<Question> {
        return com.appquests.droneprep.session.MockSampler.sampleMockQuestions(loadAllSync())
    }

    fun totalCount(): Int = loadAllSync().size

    fun topicCounts(): Map<String, Int> =
        loadAllSync().groupingBy { it.topic }.eachCount()

    fun getQuestionsByTopic(topic: String): List<Question> =
        loadAllSync().filter { it.topic == topic }

    /**
     * Loads all acronyms from assets. Should be called from a background thread.
     */
    suspend fun loadAcronyms(): List<Acronym> = withContext(Dispatchers.IO) {
        acronymCache?.let { return@withContext it }
        
        try {
            val input = context.assets.open("acronyms.json")
            val reader = BufferedReader(InputStreamReader(input))
            val json = reader.use { it.readText() }
            val acronymData: AcronymData = gson.fromJson(json, acronymDataType)
            val acronyms = acronymData.acronyms.sortedBy { it.acronym }
            acronymCache = acronyms
            acronyms
        } catch (e: Exception) {
            ErrorHandler.logError("Failed to load acronyms", e)
            emptyList()
        }
    }

    /**
     * Gets all acronyms. Use from main thread only if cache is available.
     */
    fun loadAcronymsSync(): List<Acronym> {
        acronymCache?.let { return it }
        
        return try {
            val input = context.assets.open("acronyms.json")
            val reader = BufferedReader(InputStreamReader(input))
            val json = reader.use { it.readText() }
            val acronymData: AcronymData = gson.fromJson(json, acronymDataType)
            val acronyms = acronymData.acronyms.sortedBy { it.acronym }
            acronymCache = acronyms
            acronyms
        } catch (e: Exception) {
            ErrorHandler.logError("Failed to load acronyms", e)
            emptyList()
        }
    }
}
