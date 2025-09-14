package com.appquests.droneprep.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.appquests.droneprep.ui.design.UiSize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home Screen, managing quiz settings and topic selection
 */
class HomeViewModel : BaseViewModel() {
    
    private val _isMock = MutableStateFlow(false)
    val isMock: StateFlow<Boolean> = _isMock.asStateFlow()
    
    private val _questionCount = MutableStateFlow(UiSize.MIN_QUESTIONS)
    val questionCount: StateFlow<Int> = _questionCount.asStateFlow()
    
    private val _selectedTopics = MutableStateFlow<List<String>>(emptyList())
    val selectedTopics: StateFlow<List<String>> = _selectedTopics.asStateFlow()
    
    private val _allTopics = MutableStateFlow<List<String>>(emptyList())
    val allTopics: StateFlow<List<String>> = _allTopics.asStateFlow()
    
    init {
        loadInitialData()
    }
    
    private fun loadInitialData() {
        safeCall {
            // Load saved settings
            val savedMode = settingsStore.getLastMode()
            _isMock.value = savedMode == "MOCK"
            
            val savedCount = settingsStore.getLastCount()
            _questionCount.value = savedCount.coerceIn(UiSize.MIN_QUESTIONS, UiSize.MAX_QUESTIONS)
            
            // Load topics
            val topics = questionRepository.allTopics()
            _allTopics.value = topics
            
            val savedTopics = settingsStore.getLastTopics()
            _selectedTopics.value = if (savedTopics.isEmpty()) topics else savedTopics
        }
    }
    
    fun toggleMode() {
        _isMock.value = !_isMock.value
        val mode = if (_isMock.value) "MOCK" else "PRACTICE"
        settingsStore.setLastMode(mode)
    }
    
    fun adjustQuestionCount(delta: Int) {
        if (_isMock.value) return // Can't adjust mock question count
        
        val newCount = (_questionCount.value + delta).coerceIn(UiSize.MIN_QUESTIONS, UiSize.MAX_QUESTIONS)
        _questionCount.value = newCount
        settingsStore.setLastCount(newCount)
    }
    
    fun updateSelectedTopics(topics: List<String>) {
        _selectedTopics.value = topics
        settingsStore.setLastTopics(topics)
    }
    
    fun getEffectiveQuestionCount(): Int {
        return if (_isMock.value) UiSize.MOCK_QUESTIONS else _questionCount.value
    }
    
    fun getEffectiveTopics(): List<String> {
        return _selectedTopics.value.ifEmpty { _allTopics.value }
    }
}
