package com.appquests.droneprep.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.appquests.droneprep.di.AppGraph
import com.appquests.droneprep.data.prefs.TopicTotals

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyTopicsScreen(navController: NavController) {
    val topics = remember { AppGraph.questionRepository.allTopics() }
    val counts = remember { AppGraph.questionRepository.topicCounts() }
    val mastery = remember { AppGraph.topicStatsStore.getAll() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Study by topic") }) }
    ) { inner ->
        if (topics.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(inner), contentAlignment = Alignment.Center) {
                Text("No topics available")
            }
            return@Scaffold
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.padding(inner),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(topics) { topic ->
                val count = counts[topic] ?: 0
                val totals: TopicTotals? = mastery[topic]
                val masteryText = if (totals != null && totals.total > 0) "${totals.percent}% mastery" else "No history"

                ElevatedCard(
                    onClick = {
                        // Encode topic for route; we'll replace spaces with %20 for simplicity
                        val encoded = java.net.URLEncoder.encode(topic, "UTF-8")
                        navController.navigate("studyReader/$encoded")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(topic, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "$count question(s) • $masteryText",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
