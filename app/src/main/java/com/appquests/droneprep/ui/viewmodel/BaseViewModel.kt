package com.appquests.droneprep.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.appquests.droneprep.di.AppGraph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel providing common functionality and repository access
 */
abstract class BaseViewModel : ViewModel() {
    protected val questionRepository = AppGraph.questionRepository
    protected val settingsStore = AppGraph.settingsStore
    protected val resultStore = AppGraph.resultStore
    protected val topicStatsStore = AppGraph.topicStatsStore
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    protected fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }
    
    protected fun setError(message: String?) {
        _error.value = message
    }
    
    protected fun clearError() {
        _error.value = null
    }
    
    protected fun safeCall(action: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                setLoading(true)
                clearError()
                action()
            } catch (e: Exception) {
                setError(e.message ?: "An unknown error occurred")
            } finally {
                setLoading(false)
            }
        }
    }
}

/**
 * Factory for creating ViewModels
 */
class ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel() as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
