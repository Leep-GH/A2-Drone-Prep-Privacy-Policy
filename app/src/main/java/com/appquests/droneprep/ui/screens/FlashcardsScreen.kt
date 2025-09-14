package com.appquests.droneprep.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.appquests.droneprep.di.AppGraph
import com.appquests.droneprep.data.model.Question
import com.appquests.droneprep.ui.design.Palette

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(navController: NavController) {
    val repo = remember { AppGraph.questionRepository }
    val settings = remember { AppGraph.settingsStore }

    // Topics: use last-selected, else all
    val topics = remember { settings.getLastTopics() }
    val pool: List<Question> = remember {
        val effectiveTopics = if (topics.isEmpty()) repo.allTopics() else topics
        repo.getRandomQuestions(effectiveTopics, Int.MAX_VALUE)  // get all for topics
    }
    // Cap deck to keep sessions snappy
    val deckInit = remember { if (pool.size > 60) pool.shuffled().take(60) else pool.shuffled() }

    var deck by remember { mutableStateOf(deckInit) }
    var index by rememberSaveable { mutableIntStateOf(0) }
    var showBack by rememberSaveable { mutableStateOf(false) }
    val q = deck.getOrNull(index)
    val isLast = index == deck.lastIndex
    var againQueue by remember { mutableStateOf(mutableListOf<Question>()) }

    Scaffold(
        modifier = Modifier.background(Palette.Bg),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.MenuBook,
                            contentDescription = null,
                            tint = Palette.AccentOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            if (deck.isEmpty()) "Flashcards"
                            else "Flashcards • ${index + 1}/${deck.size}",
                            color = Palette.TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Palette.Bg,
                    titleContentColor = Palette.TextPrimary
                ),
                actions = {
                    TextButton(onClick = {
                        // reshuffle current deck & restart
                        deck = deck.shuffled()
                        index = 0
                        showBack = false
                        againQueue = mutableListOf()
                    }) { Text("Shuffle") }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { inner ->
        if (deck.isEmpty() || q == null) {
            Box(Modifier.fillMaxSize().padding(inner), contentAlignment = Alignment.Center) {
                Text("No cards available")
            }
            return@Scaffold
        }

        Column(
            Modifier
                .padding(inner)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Card
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onClick = { showBack = !showBack }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = showBack,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "flip"
                    ) { back ->
                        if (!back) {
                            // FRONT: question prompt only
                            Text(
                                q.stem,
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.verticalScroll(rememberScrollState())
                            )
                        } else {
                            // BACK: answer + explanation (and optional more/ref/tip)
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    "Answer: " + q.options[q.correctIndex],
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    q.explanation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (!q.more.isNullOrBlank()) {
                                    Text(q.more!!, style = MaterialTheme.typography.bodySmall)
                                }
                                if (!q.ref.isNullOrBlank()) {
                                    Text("Ref: ${q.ref}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (!q.tip.isNullOrBlank()) {
                                    Text("Tip: ${q.tip}", style = MaterialTheme.typography.bodySmall)
                                }
                                Spacer(Modifier.height(8.dp))
                                Text("Tap card to flip", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }

            // Controls
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilledTonalButton(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 52.dp),
                    onClick = {
                        // mark for review again later
                        againQueue.add(q)
                        if (isLast) {
                            if (againQueue.isNotEmpty()) {
                                deck = againQueue.toList()
                                againQueue.clear()
                                index = 0
                            }
                        } else {
                            index += 1
                        }
                        showBack = false
                    }
                ) { Text("Again") }

                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 52.dp),
                    onClick = { showBack = !showBack }
                ) { Text(if (showBack) "Hide answer" else "Show answer") }

                Button(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 52.dp),
                    onClick = {
                        if (isLast) {
                            if (againQueue.isNotEmpty()) {
                                deck = againQueue.toList()
                                againQueue.clear()
                                index = 0
                            } else {
                                navController.popBackStack()
                            }
                        } else {
                            index += 1
                        }
                        showBack = false
                    }
                ) { Text(if (isLast && againQueue.isEmpty()) "Done" else "Got it") }
            }

            Text(
                "Tap card to flip • No scoring",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
