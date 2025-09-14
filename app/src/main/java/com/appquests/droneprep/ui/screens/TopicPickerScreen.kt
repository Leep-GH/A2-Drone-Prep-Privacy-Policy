package com.appquests.droneprep.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.appquests.droneprep.di.AppGraph
import com.appquests.droneprep.data.prefs.TopicTotals
import com.appquests.droneprep.ui.design.Palette

fun getTopicIcon(topic: String): ImageVector {
    return when {
        topic.contains("Air Law", ignoreCase = true) || topic.contains("law", ignoreCase = true) -> Icons.Filled.Info
        topic.contains("Human", ignoreCase = true) || topic.contains("performance", ignoreCase = true) -> Icons.Filled.Person
        topic.contains("Weather", ignoreCase = true) || topic.contains("meteorology", ignoreCase = true) -> Icons.Filled.Info
        topic.contains("Operations", ignoreCase = true) || topic.contains("ops", ignoreCase = true) -> Icons.Filled.Settings
        topic.contains("Navigation", ignoreCase = true) || topic.contains("nav", ignoreCase = true) -> Icons.Filled.Home
        topic.contains("Radio", ignoreCase = true) || topic.contains("communication", ignoreCase = true) -> Icons.Filled.Info
        topic.contains("Flight", ignoreCase = true) || topic.contains("planning", ignoreCase = true) -> Icons.Filled.Info
        topic.contains("Safety", ignoreCase = true) || topic.contains("emergency", ignoreCase = true) -> Icons.Filled.Info
        topic.contains("Aircraft", ignoreCase = true) || topic.contains("systems", ignoreCase = true) -> Icons.Filled.Settings
        topic.contains("Airspace", ignoreCase = true) || topic.contains("space", ignoreCase = true) -> Icons.Filled.Home
        else -> Icons.Filled.Info
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicPickerScreen(navController: NavController) {
    val allTopics = remember { AppGraph.questionRepository.allTopics() }
    val counts = remember { AppGraph.questionRepository.topicCounts() }
    val masteryMap = remember { AppGraph.topicStatsStore.getAll() }

    val previous = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<List<String>>("selectedTopics")
        ?: emptyList()

    var selected by rememberSaveable {
        mutableStateOf(previous.filter { it in allTopics })
    }

    fun toggle(topic: String) {
        selected = if (selected.contains(topic)) {
            selected - topic
        } else {
            selected + topic
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Palette.Bg)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Palette.Bg,
            bottomBar = {
                BottomAppBar(
                    containerColor = Palette.Bg,
                    actions = {
                        TextButton(onClick = { navController.popBackStack() }) {
                            Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                        }
                        Spacer(Modifier.weight(1f))
                        Button(
                            onClick = {
                                AppGraph.settingsStore.setLastTopics(selected)
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selectedTopics", selected)
                                navController.popBackStack()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Palette.AccentBlue,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                "Done", 
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                )
            }
        ) { inner ->
            Column(
                Modifier
                    .padding(inner)
                    .padding(horizontal = 16.dp)
                    .padding(top = 32.dp, bottom = 16.dp)
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { navController.popBackStack() }
                            .background(
                                color = Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Text(
                        "Choose Topics",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
                
                Spacer(Modifier.height(24.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { selected = allTopics },
                        enabled = selected.size < allTopics.size
                    ) {
                        Text("Select all", color = if (selected.size < allTopics.size) Palette.AccentBlue else Color.White.copy(alpha = 0.5f))
                    }
                    
                    Text(
                        if (selected.isEmpty()) "Choose topics" else "${selected.size} selected",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = if (selected.isEmpty()) Color.White.copy(alpha = 0.7f) else Palette.AccentBlue
                    )
                    
                    TextButton(
                        onClick = { selected = emptyList() },
                        enabled = selected.isNotEmpty()
                    ) {
                        Text("Clear", color = if (selected.isNotEmpty()) Palette.AccentBlue else Color.White.copy(alpha = 0.5f))
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (allTopics.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No topics available", color = Color.White)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(allTopics) { topic ->
                            val checked = selected.contains(topic)
                            val count = counts[topic] ?: 0
                            val totals: TopicTotals? = masteryMap[topic]
                            val masteryText = if (totals != null && totals.total > 0) "${totals.percent}% mastery" else "No history"
                            
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { toggle(topic) },
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = if (checked) 
                                        Palette.AccentBlue.copy(alpha = 0.15f) 
                                    else 
                                        Palette.Card
                                ),
                                elevation = CardDefaults.elevatedCardElevation(
                                    defaultElevation = if (checked) 6.dp else 2.dp,
                                    pressedElevation = 8.dp
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(
                                                color = if (checked) 
                                                    Palette.AccentBlue.copy(alpha = 0.2f) 
                                                else 
                                                    Palette.TextSecondary.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(24.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            getTopicIcon(topic),
                                            contentDescription = null,
                                            tint = if (checked) Palette.AccentBlue else Palette.TextSecondary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    
                                    Spacer(Modifier.width(16.dp))
                                    
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            topic, 
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = Color.White
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "$count question(s) • $masteryText",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(
                                                color = if (checked) 
                                                    Palette.AccentBlue 
                                                else 
                                                    Palette.TextSecondary.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (checked) {
                                            Icon(
                                                Icons.Rounded.Check,
                                                contentDescription = "Selected",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}