package com.appquests.droneprep.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RateReview
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.appquests.droneprep.session.QuizSessionHolder
import com.appquests.droneprep.data.model.QuizMode
import com.appquests.droneprep.ui.design.Palette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(navController: NavController) {
    val questions = remember { QuizSessionHolder.reviewQuestions() }
    val answers = remember { QuizSessionHolder.reviewAnswers() }
    val mode = remember { QuizSessionHolder.mode }

    Scaffold(
        modifier = Modifier.background(Palette.Bg),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.RateReview,
                            contentDescription = null,
                            tint = Palette.AccentOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Review answers",
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
        if (questions.isEmpty() || answers.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(inner)
                    .background(Palette.Bg),
                contentAlignment = Alignment.Center
            ) { 
                Text(
                    "Nothing to review",
                    color = Palette.TextSecondary
                ) 
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .padding(inner)
                .background(Palette.Bg)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(questions) { index, q ->
                val sel = answers.getOrNull(index)
                val correctIdx = q.correctIndex
                val isCorrect = sel != null && sel == correctIdx

                Surface(
                    color = Palette.Card,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Q${index + 1}", style = MaterialTheme.typography.labelMedium)
                        Text(q.stem, style = MaterialTheme.typography.titleMedium)

                        // Your answer
                        Text(
                            "Your answer: " + (sel?.let { q.options[it] } ?: "No answer"),
                            color = if (isCorrect) MaterialTheme.colorScheme.tertiary
                                    else MaterialTheme.colorScheme.error
                        )
                        // Correct answer
                        if (!isCorrect) {
                            Text(
                                "Correct answer: " + q.options[correctIdx],
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        // Explanation: for PRACTICE show always; for MOCK this is where users finally see it
                        Text(
                            q.explanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
            }
        }
    }
}
