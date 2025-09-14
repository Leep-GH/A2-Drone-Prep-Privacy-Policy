package com.appquests.droneprep.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import android.widget.Toast
import kotlinx.coroutines.delay
import com.appquests.droneprep.R
import com.appquests.droneprep.data.model.QuizMode
import com.appquests.droneprep.session.QuizSessionHolder
import com.appquests.droneprep.di.AppGraph
import com.appquests.droneprep.ui.design.Palette
import com.appquests.droneprep.ui.design.UiSize
import com.appquests.droneprep.util.ErrorHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(navController: NavController) {
    // Consistent typography styling
    val titleStyle = MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight.SemiBold,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )
    
    val questions = remember { QuizSessionHolder.questions }
    val mode = remember { QuizSessionHolder.mode }

    // Timer support
    val timerEnabled = remember { AppGraph.settingsStore.isMockTimerEnabled() }
    var tick by remember { mutableIntStateOf(0) }
    if (mode == QuizMode.MOCK && timerEnabled) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(1000)
                tick++ // forces recomposition
            }
        }
    }
    fun fmt(sec: Int): String {
        val m = sec / 60
        val s = sec % 60
        return "%d:%02d".format(m, s)
    }
    val elapsed = if (mode == QuizMode.MOCK && timerEnabled) QuizSessionHolder.elapsedSeconds() else 0

    if (questions.isEmpty()) {
        // No session prepared — return to home
        LaunchedEffect(Unit) { navController.popBackStack() }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No questions loaded")
        }
        return
    }

    var index by rememberSaveable { mutableIntStateOf(0) }
    var isFlagged by remember(index) { mutableStateOf(com.appquests.droneprep.session.QuizSessionHolder.isFlagged(index)) }
    var selected by rememberSaveable { mutableStateOf<Int?>(null) }
    var answered by rememberSaveable { mutableStateOf(false) }
    var showExplanation by rememberSaveable { mutableStateOf(false) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    val q = questions[index]
    val isLast = index == questions.lastIndex
    val ctx = LocalContext.current
    val progress = (index + 1f) / questions.size

    if (mode == QuizMode.MOCK) {
        BackHandler(enabled = true) {
            showExitDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Palette.Bg)
    ) {
        Column(
            Modifier.fillMaxSize()
        ) {
            // Custom header with back button and title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 32.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (mode == QuizMode.PRACTICE) {
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
                                Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                    }
                    Text(
                        if (mode == QuizMode.MOCK && timerEnabled) 
                            "Q ${index + 1}/${questions.size}  •  ${fmt(elapsed)}" 
                        else 
                            "Q ${index + 1} / ${questions.size}",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
                
                // Flag button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            com.appquests.droneprep.session.QuizSessionHolder.toggleFlag(index)
                            isFlagged = com.appquests.droneprep.session.QuizSessionHolder.isFlagged(index)
                        }
                        .background(
                            color = if (isFlagged) 
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f) 
                            else 
                                Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = if (isFlagged) "Unflag" else "Flag",
                        tint = if (isFlagged) MaterialTheme.colorScheme.tertiary else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Column(
                Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // Innovative circular progress with question counter
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressWithCounter(
                    progress = progress,
                    currentQuestion = index + 1,
                    totalQuestions = questions.size,
                    modifier = Modifier.size(70.dp)
                )
            }
            
            Text(
                q.stem, 
                style = titleStyle,
                color = Palette.TextPrimary
            )

            q.options.forEachIndexed { i, text ->
                val correct = q.correctIndex == i
                val isSelected = selected == i
                val cardColor = when {
                    !answered -> Palette.Card
                    correct -> Palette.AccentBlue
                    isSelected && !correct -> Color(0xFFF44336)
                    else -> Palette.Card
                }
                val textColor = when {
                    answered && (correct || (isSelected && !correct)) -> Color.White
                    else -> Palette.TextPrimary
                }
                
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(300, delayMillis = i * 100)
                    ) + fadeIn(animationSpec = tween(300, delayMillis = i * 100))
                ) {
                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors(containerColor = cardColor),
                        onClick = {
                            if (!answered) {
                                selected = i
                                answered = true
                                showExplanation = (mode == QuizMode.PRACTICE)
                                QuizSessionHolder.recordAnswer(index, i)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = if (isSelected && !answered) 8.dp else 2.dp
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Clean letter badge
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        color = when {
                                            !answered && isSelected -> Palette.AccentBlue
                                            answered && (correct || (isSelected && !correct)) -> Color.White.copy(alpha = 0.2f)
                                            else -> Palette.TextSecondary.copy(alpha = 0.15f)
                                        },
                                        shape = RoundedCornerShape(18.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ('A' + i).toString(),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = when {
                                        !answered && isSelected -> Color.White
                                        answered && (correct || (isSelected && !correct)) -> Color.White
                                        else -> Palette.TextSecondary
                                    }
                                )
                            }
                            
                            Spacer(Modifier.width(20.dp))
                            
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (isSelected || (answered && correct)) FontWeight.SemiBold else FontWeight.Normal
                                ),
                                color = textColor,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            if (answered && mode == QuizMode.PRACTICE) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(containerColor = Palette.Card),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { showExplanation = !showExplanation },
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Rounded.Info, 
                                    contentDescription = null,
                                    tint = Palette.AccentBlue
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "Explanation", 
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = Palette.TextPrimary
                                )
                            }
                            Text(
                                if (showExplanation) "Hide" else "Show",
                                color = Palette.AccentBlue,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                        AnimatedVisibility(visible = showExplanation, enter = fadeIn(), exit = fadeOut()) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    "Correct answer: " + q.options[q.correctIndex],
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = Palette.AccentBlue
                                )
                                Text(
                                    q.explanation, 
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Palette.TextPrimary
                                )
                                if (!q.more.isNullOrBlank()) {
                                    Text(
                                        q.more!!, 
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Palette.TextSecondary
                                    )
                                }
                                if (!q.ref.isNullOrBlank()) {
                                    Text(
                                        "Ref: ${q.ref}", 
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Palette.TextSecondary
                                    )
                                }
                                if (!q.tip.isNullOrBlank()) {
                                    Text(
                                        "Tip: ${q.tip}", 
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = Palette.AccentBlue
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (answered && mode == QuizMode.MOCK) {
                Text(
                    "Answer saved. Explanations available after you finish.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Palette.TextSecondary
                )
            }

            }
            
            // Fixed navigation buttons at bottom
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
                    .imePadding(), 
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    enabled = index > 0,
                    onClick = {
                        index -= 1
                        selected = null
                        answered = false
                        showExplanation = false
                        isFlagged = com.appquests.droneprep.session.QuizSessionHolder.isFlagged(index)
                    }
                ) { 
                    Text(
                        "Previous",
                        color = if (index > 0) Palette.TextPrimary else Palette.TextSecondary
                    ) 
                }

                Button(
                    onClick = {
                        if (!answered) {
                            Toast.makeText(ctx, "Choose an answer to continue", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (isLast) {
                            val result = QuizSessionHolder.buildResult()
                            runCatching { 
                                AppGraph.resultStore.saveResult(result) 
                            }.onFailure { throwable ->
                                ErrorHandler.logError("Failed to save quiz result", throwable)
                                Toast.makeText(ctx, ctx.getString(R.string.error_saving_result), Toast.LENGTH_LONG).show()
                            }
                            navController.navigate("results")
                        } else {
                            index += 1
                            selected = null
                            answered = false
                            showExplanation = false
                            isFlagged = com.appquests.droneprep.session.QuizSessionHolder.isFlagged(index)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Palette.AccentBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) { 
                    Text(
                        if (isLast) "Finish" else "Next",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    ) 
                }
            }

            if (showExitDialog) {
                AlertDialog(
                    onDismissRequest = { showExitDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            // End mock now: build result, save, and navigate to Results
                            val result = com.appquests.droneprep.session.QuizSessionHolder.buildResult()
                            runCatching { 
                                com.appquests.droneprep.di.AppGraph.resultStore.saveResult(result) 
                            }.onFailure { throwable ->
                                ErrorHandler.logError("Failed to save quiz result during early exit", throwable)
                                // Still navigate to results even if save fails
                            }
                            showExitDialog = false
                            navController.navigate("results")
                        }) { Text(stringResource(R.string.end_mock)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showExitDialog = false }) { Text(stringResource(R.string.resume)) }
                    },
                    title = { Text(stringResource(R.string.end_mock_title)) },
                    text = { Text(stringResource(R.string.end_mock_message)) }
                )
            }
        }
    }
}

@Composable
private fun CircularProgressWithCounter(
    progress: Float,
    currentQuestion: Int,
    totalQuestions: Int,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600, easing = EaseOutCubic),
        label = "progress"
    )
    
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 6.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)
            
            // Background circle
            drawCircle(
                color = Color.Gray.copy(alpha = 0.2f),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // Progress arc with Palette color
            drawArc(
                color = Color(0xFF2196F3), // Palette.AccentBlue equivalent
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
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = currentQuestion.toString(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                ),
                color = Palette.AccentBlue
            )
            Text(
                text = "of $totalQuestions",
                style = MaterialTheme.typography.bodySmall,
                color = Palette.TextSecondary
            )
        }
    }
}