package com.appquests.droneprep.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.animation.core.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.PlatformTextStyle

import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.appquests.droneprep.R
import com.appquests.droneprep.session.QuizSessionHolder
import com.appquests.droneprep.session.TopicStat
import com.appquests.droneprep.data.model.QuizMode
import com.appquests.droneprep.di.AppGraph
import com.appquests.droneprep.ui.design.Palette
import com.appquests.droneprep.ui.design.UiSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(navController: NavController) {
    // Consistent typography styling
    val titleStyle = MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight.SemiBold,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )
    
    val result = remember { QuizSessionHolder.lastResult }

    Scaffold(
        modifier = Modifier.background(Palette.Bg),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Results",
                            color = Palette.TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = Palette.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Palette.Bg,
                    titleContentColor = Palette.TextPrimary
                )
            )
        }
    ) { paddingValues ->
        if (result == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues), 
                contentAlignment = Alignment.Center
            ) {
                Text("No result available", color = Palette.TextPrimary)
            }
            return@Scaffold
        }

        val percent = if (result.total > 0) (result.correct * 100 / result.total) else 0
        val passMark = UiSize.PASS_MARK
        val isMock = result.mode == QuizMode.MOCK
        val passed = percent >= passMark

        val breakdown: List<TopicStat> = remember { QuizSessionHolder.topicBreakdown() }

        LaunchedEffect(result.id) {
            // Merge this session's breakdown into persistent totals
            AppGraph.topicStatsStore.applyBreakdown(breakdown)
        }

        Column(
            Modifier
                .fillMaxSize()
                .background(Palette.Bg)
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // Circular score display with encouraging message
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                // Encouraging message based on score
                val (message, emoji) = when {
                    percent >= 90 -> "Excellent work!" to "🎉"
                    percent >= 80 -> "Great job!" to "👏"
                    percent >= 70 -> "Well done!" to "👍"
                    percent >= 60 -> "Good effort!" to "💪"
                    percent >= 50 -> "Keep practicing!" to "📚"
                    else -> "Don't give up!" to "🚀"
                }
                
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                        initialOffsetY = { -it / 2 },
                        animationSpec = tween(800)
                    )
                ) {
                    Text(
                        "$emoji $message",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Palette.AccentBlue
                    )
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Circular progress with score
                Box(
                    modifier = Modifier.size(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularScoreIndicator(
                        progress = percent / 100f,
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "$percent%",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Bold,
                                platformStyle = PlatformTextStyle(includeFontPadding = false)
                            ),
                            color = Color.White
                        )
                        Text(
                            "${result.correct}/${result.total}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            "correct answers",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            // Clean session info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${result.durationSeconds}s",
                        style = titleStyle,
                        color = Color.White
                    )
                    Text(
                        "Duration",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        result.mode.toString(),
                        style = titleStyle,
                        color = Color.White
                    )
                    Text(
                        "Mode",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${result.topics.size}",
                        style = titleStyle,
                        color = Color.White
                    )
                    Text(
                        "Topics",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            if (isMock) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (passed) Palette.AccentBlue.copy(alpha = 0.1f) else Color(0xFFF44336).copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (passed) "PASS" else "FAIL",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                platformStyle = PlatformTextStyle(includeFontPadding = false)
                            ),
                            color = if (passed) Palette.AccentBlue else Color(0xFFF44336)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "(${if (passed) "≥" else "<"} $passMark%)",
                            style = MaterialTheme.typography.titleMedium,
                            color = Palette.TextSecondary
                        )
                    }
                }
            }

            // Simple topic breakdown - no scrolling
            if (breakdown.isNotEmpty()) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = Palette.Card
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        breakdown.forEach { stat ->
                            val topicPercent = if (stat.total > 0) stat.correct.toFloat() / stat.total.toFloat() else 0f
                            
                            // Calculate gradient color for this topic's performance
                            fun lerp(start: Float, stop: Float, fraction: Float): Float {
                                return start + fraction * (stop - start)
                            }
                            
                            fun lerpColor(startColor: Color, endColor: Color, fraction: Float): Color {
                                return Color(
                                    red = lerp(startColor.red, endColor.red, fraction),
                                    green = lerp(startColor.green, endColor.green, fraction),
                                    blue = lerp(startColor.blue, endColor.blue, fraction),
                                    alpha = lerp(startColor.alpha, endColor.alpha, fraction)
                                )
                            }
                            
                            val topicColor = when {
                                topicPercent <= 0.2f -> {
                                    val fraction = topicPercent / 0.2f
                                    lerpColor(Color(0xFFDC2626), Color(0xFFEA580C), fraction)
                                }
                                topicPercent <= 0.4f -> {
                                    val fraction = (topicPercent - 0.2f) / 0.2f
                                    lerpColor(Color(0xFFEA580C), Color(0xFFD97706), fraction)
                                }
                                topicPercent <= 0.6f -> {
                                    val fraction = (topicPercent - 0.4f) / 0.2f
                                    lerpColor(Color(0xFFD97706), Color(0xFFCA8A04), fraction)
                                }
                                topicPercent <= 0.8f -> {
                                    val fraction = (topicPercent - 0.6f) / 0.2f
                                    lerpColor(Color(0xFFCA8A04), Color(0xFF65A30D), fraction)
                                }
                                else -> {
                                    val fraction = (topicPercent - 0.8f) / 0.2f
                                    lerpColor(Color(0xFF65A30D), Color(0xFF16A34A), fraction)
                                }
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    stat.topic,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = Color.White
                                )
                                Text(
                                    "${stat.correct}/${stat.total}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = topicColor
                                )
                            }
                        }
                    }
                }
            }

            // Clean action buttons
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = { navController.navigate("history") },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Palette.TextPrimary
                    )
                ) {
                    Text(
                        "History",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = { 
                        QuizSessionHolder.clearReviewSubset()
                        navController.navigate("review") 
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Palette.AccentBlue
                    )
                ) {
                    Text(
                        "Review",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // Flagged questions review (if any)
            val flaggedCount = remember { QuizSessionHolder.flaggedCount() }
            if (flaggedCount > 0) {
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    onClick = { 
                        QuizSessionHolder.startFlaggedReview()
                        navController.navigate("review") 
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Palette.AccentBlue
                    )
                ) {
                    Text(
                        "Review Flagged ($flaggedCount)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // Refined Home button
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                onClick = {
                    QuizSessionHolder.clear()
                    navController.popBackStack(route = "home", inclusive = false)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Palette.AccentBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Back to Home",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
private fun CircularScoreIndicator(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1200, easing = EaseOutCubic),
        label = "score_progress"
    )
    
    Canvas(modifier = modifier) {
        val strokeWidth = 14.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)
        
        // Background circle
        drawCircle(
            color = Color.Gray.copy(alpha = 0.15f),
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        
        // Simple solid color based on performance
        val progressColor = when {
            progress >= 0.8f -> Color(0xFF22C55E) // Green for 80%+
            progress >= 0.6f -> Color(0xFFEAB308) // Yellow for 60-79%
            progress >= 0.4f -> Color(0xFFF97316) // Orange for 40-59%
            else -> Color(0xFFEF4444) // Red for <40%
        }
        
        // Draw the progress arc
        drawArc(
            color = progressColor,
            startAngle = -90f,
            sweepAngle = animatedProgress * 360f,
            useCenter = false,
            topLeft = Offset(
                center.x - radius,
                center.y - radius
            ),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}
