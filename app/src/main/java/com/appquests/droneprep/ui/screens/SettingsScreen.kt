package com.appquests.droneprep.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.appquests.droneprep.di.AppGraph
import com.appquests.droneprep.ui.design.Palette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(_navController: NavController) {
    val settings = remember { AppGraph.settingsStore }
    val resultStore = remember { AppGraph.resultStore }
    val statsStore = remember { AppGraph.topicStatsStore }
    
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showResetMasteryDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        modifier = Modifier.background(Palette.Bg),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.Settings,
                            contentDescription = null,
                            tint = Palette.AccentOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Settings",
                            color = Palette.TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Palette.Bg,
                    titleContentColor = Palette.TextPrimary
                )
            )
        }
    ) { inner ->
        val scroll = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(inner)
                .background(Palette.Bg)
                .verticalScroll(scroll)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tagline
            Text(
                "Configure your study preferences",
                style = MaterialTheme.typography.bodySmall,
                color = Palette.TextSecondary
            )
            // Quiz card
            Surface(
                color = Palette.Card,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Quiz",
                        style = MaterialTheme.typography.titleMedium,
                        color = Palette.TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Mock exam timer",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Palette.TextSecondary
                        )
                        
                        var timerEnabled by remember { mutableStateOf(settings.isMockTimerEnabled()) }
                        Switch(
                            checked = timerEnabled,
                            onCheckedChange = { enabled ->
                                timerEnabled = enabled
                                settings.setMockTimerEnabled(enabled)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Palette.AccentBlue,
                                checkedTrackColor = Palette.AccentBlue.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
            
            // Data card
            Surface(
                color = Palette.Card,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Data",
                        style = MaterialTheme.typography.titleMedium,
                        color = Palette.TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    OutlinedButton(
                        onClick = { showClearHistoryDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Palette.TextSecondary
                        )
                    ) {
                        Text("Clear history")
                    }
                    
                    OutlinedButton(
                        onClick = { showResetMasteryDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Palette.TextSecondary
                        )
                    ) {
                        Text("Reset topic mastery")
                    }
                }
            }
            Surface(
                color = Palette.Card,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Privacy", 
                        style = MaterialTheme.typography.titleMedium,
                        color = Palette.TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "This app operates completely offline. No personal data is collected, " +
                        "stored remotely, or shared with third parties. All quiz history and " +
                        "preferences are stored locally on your device.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Palette.TextSecondary
                    )
                }
            }
            
            Surface(
                color = Palette.Card,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "About", 
                        style = MaterialTheme.typography.titleMedium,
                        color = Palette.TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "A2 CofC Drone Prep is an independent study tool for the UK A2 Certificate " +
                        "of Competency. It is not affiliated with the UK CAA or any training organisation.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Palette.TextSecondary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Always refer to official CAA guidance and the Drone Code for current regulations.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Palette.TextDisabled
                    )
                }
            }
            
            // Footer spacing
            Spacer(Modifier.height(24.dp))
        }
        
        // Confirmation dialogs
        if (showClearHistoryDialog) {
            AlertDialog(
                onDismissRequest = { showClearHistoryDialog = false },
                title = { Text("Clear History") },
                text = { Text("This will permanently delete all quiz results and history. This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            resultStore.clear()
                            showClearHistoryDialog = false
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Palette.Error)
                    ) {
                        Text("Clear")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearHistoryDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        
        if (showResetMasteryDialog) {
            AlertDialog(
                onDismissRequest = { showResetMasteryDialog = false },
                title = { Text("Reset Topic Mastery") },
                text = { Text("This will reset all topic statistics and mastery progress. This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            statsStore.clear()
                            showResetMasteryDialog = false
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Palette.Error)
                    ) {
                        Text("Reset")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetMasteryDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
