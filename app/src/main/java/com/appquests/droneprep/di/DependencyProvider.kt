package com.appquests.droneprep.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.appquests.droneprep.data.repo.QuestionRepository
import com.appquests.droneprep.data.prefs.ResultStore
import com.appquests.droneprep.data.prefs.SettingsStore
import com.appquests.droneprep.data.prefs.TopicStatsStore

/**
 * Improved dependency injection using Composition Locals
 */

val LocalQuestionRepository = compositionLocalOf<QuestionRepository> {
    error("QuestionRepository not provided")
}

val LocalResultStore = compositionLocalOf<ResultStore> {
    error("ResultStore not provided")
}

val LocalSettingsStore = compositionLocalOf<SettingsStore> {
    error("SettingsStore not provided")
}

val LocalTopicStatsStore = compositionLocalOf<TopicStatsStore> {
    error("TopicStatsStore not provided")
}

/**
 * Provides dependency injection for the entire app
 */
@Composable
fun DependencyProvider(
    context: Context,
    content: @Composable () -> Unit
) {
    val questionRepository = remember { QuestionRepository(context.applicationContext) }
    val resultStore = remember { ResultStore(context.applicationContext) }
    val settingsStore = remember { SettingsStore(context.applicationContext) }
    val topicStatsStore = remember { TopicStatsStore(context.applicationContext) }
    
    CompositionLocalProvider(
        LocalQuestionRepository provides questionRepository,
        LocalResultStore provides resultStore,
        LocalSettingsStore provides settingsStore,
        LocalTopicStatsStore provides topicStatsStore,
        content = content
    )
}

/**
 * Helper composables for accessing dependencies
 */
@Composable
fun questionRepository() = LocalQuestionRepository.current

@Composable  
fun resultStore() = LocalResultStore.current

@Composable
fun settingsStore() = LocalSettingsStore.current

@Composable
fun topicStatsStore() = LocalTopicStatsStore.current
