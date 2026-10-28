package com.example.remind_me.feature.lockscreen.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remind_me.core.model.ChapterItem
import com.example.remind_me.core.model.QuizQuestion
import com.example.remind_me.core.model.TopicItem
import com.example.remind_me.core.theme.RemindMeTheme
import com.example.remind_me.feature.lockscreen.logic.LockScreenQuizStateHolder
import com.example.remind_me.feature.lockscreen.logic.rememberLockScreenQuizStateHolder
import kotlinx.coroutines.delay

@Composable
fun LockScreenQuizScreen(
    question: QuizQuestion,
    topic: TopicItem?,
    chapter: ChapterItem? = null,
    streak: Int,
    requireCorrectToDismiss: Boolean,
    onAnswerSubmitted: (Boolean) -> Unit,
    onNextQuestion: () -> Unit,
    onDismissLockScreen: () -> Unit
) {
    val strings = RemindMeTheme.strings
    val stateHolder = rememberLockScreenQuizStateHolder(
        question = question,
        topic = topic,
        chapter = chapter,
        streak = streak,
        requireCorrectToDismiss = requireCorrectToDismiss,
        languageCode = strings.languageCode,
        onAnswerSubmitted = onAnswerSubmitted,
        onNextQuestion = onNextQuestion,
        onDismissLockScreen = onDismissLockScreen
    )
    LockScreenQuizContent(stateHolder = stateHolder)
}

@Composable
fun LockScreenQuizContent(
    stateHolder: LockScreenQuizStateHolder
) {
    val strings = RemindMeTheme.strings
    val question = stateHolder.question
    val topic = stateHolder.topic
    val chapter = stateHolder.chapter

    LaunchedEffect(stateHolder) {
        while (true) {
            stateHolder.refreshClock()
            delay(10_000L)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A192F),
                        Color(0xFF0F2942),
                        Color(0xFF071A2C)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar with Streak and optional Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = strings.streakLabel,
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = strings.formatLockScreenStreak(stateHolder.streak),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (!stateHolder.requireCorrectToDismiss) {
                    TextButton(onClick = stateHolder.onDismissLockScreen) {
                        Text(
                            text = strings.skipButton,
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Lock screen Clock & Date
            Text(
                text = stateHolder.currentTime,
                fontSize = 52.sp,
                fontWeight = FontWeight.Light,
                color = Color.White,
                letterSpacing = 2.sp
            )
            Text(
                text = stateHolder.currentDate,
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Topic & Chapter Badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF00ACC1).copy(alpha = 0.22f),
                border = BorderStroke(1.dp, Color(0xFF26C6DA).copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = topic?.title ?: strings.defaultTopicFallbackTitle,
                        color = Color(0xFF80DEEA),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (chapter != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = chapter.title,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Question Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = strings.lockScreenQuizHeaderBadge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4DD0E1),
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = question.question,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        lineHeight = 26.sp
                    )
                    if (question.codeSnippet.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF050E1A),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
                        ) {
                            Text(
                                text = question.codeSnippet,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = Color(0xFF80CBC4),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Multiple Choice Options (A / B / C / D)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                question.options.forEachIndexed { index, optionText ->
                    val isSelected = stateHolder.selectedOptionIndex == index
                    val isCorrectOption = index == question.correctOptionIndex
                    val showResult = stateHolder.selectedOptionIndex != null

                    val backgroundColor = when {
                        !showResult -> Color.White.copy(alpha = 0.08f)
                        isSelected && isCorrectOption -> Color(0xFF2E7D32).copy(alpha = 0.85f)
                        isSelected && !isCorrectOption -> Color(0xFFC62828).copy(alpha = 0.85f)
                        stateHolder.unlockedByCorrectAnswer && isCorrectOption -> Color(0xFF2E7D32).copy(alpha = 0.85f)
                        else -> Color.White.copy(alpha = 0.06f)
                    }

                    val borderColor = when {
                        !showResult -> Color.White.copy(alpha = 0.18f)
                        isSelected && isCorrectOption -> Color(0xFF66BB6A)
                        isSelected && !isCorrectOption -> Color(0xFFEF5350)
                        else -> Color.White.copy(alpha = 0.12f)
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(enabled = !stateHolder.unlockedByCorrectAnswer) {
                                stateHolder.selectOption(index)
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = backgroundColor,
                        border = BorderStroke(1.5.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stateHolder.optionLabels.getOrElse(index) { "${index + 1}" },
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Text(
                                text = optionText,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )

                            if (showResult && isSelected) {
                                Icon(
                                    imageVector = if (isCorrectOption) Icons.Default.CheckCircle else Icons.Default.Close,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Feedback & Explanation
            AnimatedVisibility(visible = stateHolder.selectedOptionIndex != null) {
                val isCurrentSelectionCorrect = stateHolder.isCurrentSelectionCorrect
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = if (isCurrentSelectionCorrect) {
                            Color(0xFF1B5E20).copy(alpha = 0.55f)
                        } else {
                            Color(0xFFB71C1C).copy(alpha = 0.45f)
                        },
                        border = BorderStroke(
                            1.dp,
                            if (isCurrentSelectionCorrect) Color(0xFF81C784) else Color(0xFFE57373)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = strings.explanationPrefix,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = if (isCurrentSelectionCorrect) {
                                        strings.lockScreenCorrectFeedback
                                    } else {
                                        strings.lockScreenWrongFeedback
                                    },
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                if (question.explanation.isNotBlank() && (isCurrentSelectionCorrect || !stateHolder.requireCorrectToDismiss)) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = question.explanation,
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 13.sp,
                                        lineHeight = 19.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons (Unlock / Next Question)
            if (stateHolder.canDismiss && stateHolder.selectedOptionIndex != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = stateHolder.onNextQuestion,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.anotherQuestionButton, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = stateHolder.onDismissLockScreen,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00ACC1),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.unlockNowButton, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Text(
                    text = if (stateHolder.requireCorrectToDismiss) {
                        strings.lockScreenInstructionRequireCorrect
                    } else {
                        strings.lockScreenInstructionOptional
                    },
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}
