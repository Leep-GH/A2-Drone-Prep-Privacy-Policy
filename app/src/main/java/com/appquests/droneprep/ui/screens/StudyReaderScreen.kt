package com.appquests.droneprep.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.appquests.droneprep.di.AppGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyReaderScreen(navController: NavController, topic: String) {
    // Decode the topic passed in the route
    val decodedTopic = remember(topic) { java.net.URLDecoder.decode(topic, "UTF-8") }
    val questions = remember(decodedTopic) { AppGraph.questionRepository.getQuestionsByTopic(decodedTopic) }

    var index by rememberSaveable { mutableIntStateOf(0) }

    if (questions.isEmpty()) {
        Scaffold(
            topBar = { TopAppBar(title = { Text(decodedTopic) }) }
        ) { inner ->
            Box(Modifier.fillMaxSize().padding(inner), contentAlignment = Alignment.Center) {
                Text("No questions in this topic")
            }
        }
        return
    }

    val q = questions[index]
    val isLast = index == questions.lastIndex

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${decodedTopic} • ${index + 1}/${questions.size}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stem
            Text(q.stem, style = MaterialTheme.typography.titleLarge)

            // Options (show correct highlighting; no tapping in Study)
            q.options.forEachIndexed { i, text ->
                val correct = q.correctIndex == i
                val color = if (correct) MaterialTheme.colorScheme.tertiaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(containerColor = color),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
                }
            }

            // Explanation block (always visible in Study)
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Explanation", style = MaterialTheme.typography.titleMedium)
                    Text("Correct answer: " + q.options[q.correctIndex], style = MaterialTheme.typography.bodyMedium)
                    Text(q.explanation, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (!q.more.isNullOrBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(q.more!!, style = MaterialTheme.typography.bodySmall)
                    }
                    if (!q.ref.isNullOrBlank()) {
                        Spacer(Modifier.height(2.dp))
                        Text("Ref: ${q.ref}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (!q.tip.isNullOrBlank()) {
                        Spacer(Modifier.height(2.dp))
                        Text("Tip: ${q.tip}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Nav controls
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedButton(
                    enabled = index > 0,
                    onClick = { index -= 1 }
                ) { Text("Previous") }

                Button(
                    onClick = {
                        if (isLast) navController.popBackStack()
                        else index += 1
                    }
                ) { Text(if (isLast) "Done" else "Next") }
            }
        }
    }
}
