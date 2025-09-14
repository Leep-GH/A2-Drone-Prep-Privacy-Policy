@file:OptIn(ExperimentalMaterial3Api::class)

package com.appquests.droneprep.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.appquests.droneprep.R

data class TopicUi(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

@Composable
fun TopicPickerScreen(
    topics: List<TopicUi> = listOf(
        TopicUi("airlaw", "Air Law", "3 questions • No history", Icons.Rounded.Gavel),
        TopicUi("human", "Human Performance", "3 questions • No history", Icons.Rounded.Person),
        TopicUi("ops", "Operations", "3 questions • No history", Icons.Rounded.Settings),
        TopicUi("weather", "Weather", "3 questions • No history", Icons.Rounded.Cloud),
    ),
    selectedIds: Set<String> = emptySet(),
    onCancel: () -> Unit = {},
    onDone: (Set<String>) -> Unit = {}
) {
    var current by remember { mutableStateOf(selectedIds.toMutableSet()) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) { 
                        Text(stringResource(R.string.cancel)) 
                    }
                    Button(
                        onClick = { onDone(current) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) { 
                        Text(stringResource(R.string.done)) 
                    }
                }
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Top bar
            Text(
                stringResource(R.string.choose_topics),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
            )

            // Quick actions row with clean counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { current = topics.map { it.id }.toMutableSet() }) {
                    Text(stringResource(R.string.select_all), color = MaterialTheme.colorScheme.primary)
                }

                Text(
                    stringResource(R.string.selected_count, current.size),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(onClick = { current.clear() }) {
                    Text(stringResource(R.string.clear), color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(Modifier.height(12.dp))

            // List with better spacing
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(topics) { topic ->
                    TopicRow(
                        topic = topic,
                        checked = current.contains(topic.id),
                        onToggle = {
                            if (current.contains(topic.id)) current.remove(topic.id)
                            else current.add(topic.id)
                        }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) } // breathing room above bottom bar
            }
        }
    }
}

@Composable
private fun TopicRow(
    topic: TopicUi,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                topic.icon, 
                contentDescription = null, 
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            
            Spacer(Modifier.width(16.dp))
            
            Column(Modifier.weight(1f)) {
                Text(
                    topic.title, 
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Text(
                    topic.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
            
            Checkbox(
                checked = checked, 
                onCheckedChange = { onToggle() }
            )
        }
    }
}