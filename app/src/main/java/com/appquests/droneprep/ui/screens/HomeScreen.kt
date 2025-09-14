package com.appquests.droneprep.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.appquests.droneprep.R
import com.appquests.droneprep.ui.design.Palette
import com.appquests.droneprep.ui.design.DS
import com.appquests.droneprep.ui.design.UiSize
import com.appquests.droneprep.ui.components.ToolRow

import com.appquests.droneprep.di.AppGraph
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

import com.appquests.droneprep.data.model.QuizMode
import com.appquests.droneprep.session.QuizSessionHolder

@Composable
private fun HeroHeader() {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "A2 CofC Drone Prep",
            color = Palette.TextPrimary,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            "Smart prep for your A2 CofC exam",
            color = Palette.TextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    // Constants for stepper
    val MINUS = "−" // U+2212 true minus
    
    // Number style for consistent typography
    val numberStyle = MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight.SemiBold,
        fontFeatureSettings = "tnum, lnum", // tabular digits
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )
    // Stores / repo
    val settings = remember { AppGraph.settingsStore }
    val repo = remember { AppGraph.questionRepository }

    // State (restored from SettingsStore) - Practice is default
    var isMock by rememberSaveable { mutableStateOf(false) }
    var questionCount by rememberSaveable { mutableStateOf(settings.getLastCount().coerceIn(UiSize.MIN_QUESTIONS, UiSize.MAX_QUESTIONS)) }

    // Selected topics coming back from TopicPicker
    val defaultSelected = emptyList<String>()
    val selectedTopicsFlow = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("selectedTopics", defaultSelected)
    val selectedTopics by (selectedTopicsFlow?.collectAsState(initial = defaultSelected) ?: remember { mutableStateOf(defaultSelected) })

    // Persisted topics fallback
    val persistedTopics = remember { settings.getLastTopics() }
    val effectiveSelectedTopics: List<String> =
        if (selectedTopics.isEmpty() && persistedTopics.isNotEmpty()) persistedTopics else selectedTopics

    // Helpers
    fun startAction() {
        val qs = if (isMock) {
            // Use weighted sampling for mock exams
            try { repo.getMockExamQuestions() } catch (_: Exception) { emptyList() }
        } else {
            // Use regular sampling for practice
            val topics = if (effectiveSelectedTopics.isEmpty()) repo.allTopics() else effectiveSelectedTopics
            try { repo.getRandomQuestions(topics, questionCount) } catch (_: Exception) { emptyList() }
        }
        
        if (qs.isEmpty()) return
        settings.setLastTopics(effectiveSelectedTopics)
        settings.setLastMode(if (isMock) "MOCK" else "PRACTICE")
        if (!isMock) settings.setLastCount(questionCount)
        QuizSessionHolder.startSession(qs, if (isMock) QuizMode.MOCK else QuizMode.PRACTICE)
        navController.navigate("quiz")
    }



    Scaffold(
        modifier = Modifier.background(Palette.Bg),
        topBar = {
            TopAppBar(
                title = { /* empty title */ },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Filled.Settings, null, tint = Palette.TextPrimary)
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
            Modifier
                .padding(inner)
                .background(Palette.Bg)
                .verticalScroll(scroll)
                .padding(horizontal = DS.SpaceLg)
                .padding(bottom = DS.SpaceSm)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(DS.SpaceLg)
        ) {
            // Header - centered and prominent (moved up)
            HeroHeader()

            // Quiz Mode Selection - compact row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = if (!isMock) Palette.AccentBlue.copy(alpha = 0.22f) else Palette.Card,
                    shape = RoundedCornerShape(DS.RadiusCard),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        stringResource(R.string.practice),
                        modifier = Modifier
                            .clickable {
                                isMock = false
                                settings.setLastMode("PRACTICE")
                            }
                            .padding(vertical = 16.dp)
                            .fillMaxWidth(),
                        color = if (!isMock) Palette.TextPrimary else Palette.TextSecondary,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (!isMock) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        textAlign = TextAlign.Center
                    )
                }
                Surface(
                    color = if (isMock) Palette.AccentBlue.copy(alpha = 0.22f) else Palette.Card,
                    shape = RoundedCornerShape(DS.RadiusCard),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        stringResource(R.string.mock_exam),
                        modifier = Modifier
                            .clickable {
                                isMock = true
                                settings.setLastMode("MOCK")
                            }
                            .padding(vertical = 16.dp)
                            .fillMaxWidth(),
                        color = if (isMock) Palette.TextPrimary else Palette.TextSecondary,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (isMock) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Compact Settings Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Number of Questions Box
                Surface(
                    color = Palette.Card,
                    shape = RoundedCornerShape(DS.RadiusCard),
                    modifier = Modifier
                        .weight(1f)
                        .height(UiSize.cardHeight)
                ) {
                    Column(
                        Modifier
                            .padding(DS.SpaceMd)
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Top label
                        Text(
                            stringResource(R.string.questions),
                            color = Palette.TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                        
                        Spacer(Modifier.height(8.dp))
                        
                        // Main content area - fixed height for alignment
                        Box(
                            modifier = Modifier.height(UiSize.counterBoxHeight),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Fixed height boxes for perfect alignment
                                Box(
                                    modifier = Modifier
                                        .size(UiSize.iconSize)
                                        .clickable(enabled = !isMock && questionCount > UiSize.MIN_QUESTIONS) {
                                            questionCount = (questionCount - 1).coerceIn(UiSize.MIN_QUESTIONS, UiSize.MAX_QUESTIONS)
                                            settings.setLastCount(questionCount)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = MINUS,
                                        style = numberStyle,
                                        color = if (!isMock && questionCount > UiSize.MIN_QUESTIONS) Palette.TextPrimary else Palette.TextSecondary
                                    )
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .widthIn(min = 36.dp)
                                        .height(UiSize.counterBoxHeight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isMock) UiSize.MOCK_QUESTIONS.toString() else questionCount.toString(),
                                        style = numberStyle,
                                        color = Palette.TextPrimary
                                    )
                                }
                                
                                Box(
                                    modifier = Modifier
                                        .size(UiSize.iconSize)
                                        .clickable(enabled = !isMock && questionCount < UiSize.MAX_QUESTIONS) {
                                            questionCount = (questionCount + 1).coerceIn(UiSize.MIN_QUESTIONS, UiSize.MAX_QUESTIONS)
                                            settings.setLastCount(questionCount)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+",
                                        style = numberStyle,
                                        color = if (!isMock && questionCount < UiSize.MAX_QUESTIONS) Palette.TextPrimary else Palette.TextSecondary
                                    )
                                }
                            }
                        }
                        
                        Spacer(Modifier.height(8.dp))
                        
                        // Bottom description
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val countForEst = if (isMock) 30 else questionCount
                            val estMin = maxOf(1, (countForEst * 40) / 60)
                            Text(
                                "Est. ${estMin} min",
                                color = Palette.TextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                            // Empty space to match Topics box structure
                            Text(
                                "",
                                color = Palette.AccentBlue,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp)
                            )
                        }
                    }
                }

                // Topics Box - clickable to open topic selection
                Surface(
                    color = Palette.Card,
                    shape = RoundedCornerShape(DS.RadiusCard),
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                        .clickable { 
                            // Navigate to topic picker screen
                            navController.navigate("topicPicker")
                        }
                ) {
                    Column(
                        Modifier
                            .padding(DS.SpaceMd)
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Top label
                        Text(
                            "Topics",
                            color = Palette.TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                        
                        Spacer(Modifier.height(8.dp))
                        
                        // Main content area - fixed height for alignment
                        Box(
                            modifier = Modifier.height(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (effectiveSelectedTopics.isEmpty()) "All" else "${effectiveSelectedTopics.size}",
                                color = Palette.TextPrimary,
                                style = numberStyle
                            )
                        }
                        
                        Spacer(Modifier.height(8.dp))
                        
                        // Bottom description
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                if (effectiveSelectedTopics.isEmpty()) "topics" else "selected",
                                color = Palette.TextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                "Tap to change",
                                color = Palette.AccentBlue,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp)
                            )
                        }
                    }
                }
            }

            // PRIMARY CTA
            Button(
                onClick = { startAction() },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(DS.MinButtonHeight),
                shape = RoundedCornerShape(DS.RadiusPill),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Palette.BrandYellow,
                    contentColor = Palette.Bg
                )
            ) {
                Text("Start", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }

            ToolRow(
                icon = Icons.Filled.History,
                iconTint = Palette.AccentBlue,
                title = "History",
                subtitle = "View your past quiz results and progress",
                onClick = { navController.navigate("history") }
            )

            ToolRow(
                icon = Icons.Filled.CenterFocusStrong,
                iconTint = Palette.AccentBlue,
                title = "Targeted Practice",
                subtitle = "Focus on your weakest areas",
                onClick = {
                    val weakest = AppGraph.topicStatsStore.weakestTopics(2)
                    val topicsForWeak = if (weakest.isNotEmpty()) weakest
                        else if (effectiveSelectedTopics.isNotEmpty()) effectiveSelectedTopics
                        else repo.allTopics()
                    val qs = try { repo.getRandomQuestions(topicsForWeak, 10) } catch (_: Exception) { emptyList() }
                    if (qs.isNotEmpty()) {
                        com.appquests.droneprep.session.QuizSessionHolder.startSession(qs, com.appquests.droneprep.data.model.QuizMode.PRACTICE)
                        navController.navigate("quiz")
                    }
                }
            )

            ToolRow(
                icon = Icons.Filled.MenuBook,
                iconTint = Palette.AccentBlue,
                title = "Flashcards",
                subtitle = "Browse with explanations (no scoring)",
                onClick = { navController.navigate("flashcards") }
            )

            ToolRow(
                icon = Icons.Filled.Book,
                iconTint = Palette.AccentBlue,
                title = "Acronym Buster",
                subtitle = "Quickly look up aviation terms",
                onClick = { navController.navigate("acronymBuster") }
            )

            // No footer text
            Spacer(Modifier.height(24.dp))
        }
    }
}