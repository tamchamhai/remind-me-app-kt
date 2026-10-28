package com.example.remind_me.feature.practice.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remind_me.core.model.QuizQuestion
import com.example.remind_me.core.model.TopicItem
import com.example.remind_me.core.theme.RemindMeTheme
import com.example.remind_me.feature.practice.logic.PracticeStateHolder
import com.example.remind_me.feature.practice.logic.rememberPracticeStateHolder

@Composable
fun PracticeScreen(
    questions: List<QuizQuestion>,
    topics: List<TopicItem>,
    onAnswerRecorded: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val stateHolder = rememberPracticeStateHolder(
        questions = questions,
        topics = topics,
        onAnswerRecorded = onAnswerRecorded
    )
    PracticeContent(
        stateHolder = stateHolder,
        modifier = modifier
    )
}

@Composable
fun PracticeContent(
    stateHolder: PracticeStateHolder,
    modifier: Modifier = Modifier
) {
    val colors = RemindMeTheme.colors
    val strings = RemindMeTheme.strings

    val currentQuestion = stateHolder.currentQuestion
    if (currentQuestion == null) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.border)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = strings.emptyPracticeTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = strings.emptyPracticeSubtitle,
                    fontSize = 14.sp,
                    color = colors.textSecondary
                )
            }
        }
        return
    }

    val topicTitle = stateHolder.resolveTopicTitle(strings.tabTopics)
    val selectedOption = stateHolder.selectedOption

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.border),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = colors.primaryContainer
                ) {
                    Text(
                        text = topicTitle,
                        color = colors.onPrimaryContainer,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }
                Text(
                    text = strings.formatPracticeQuestionCounter(
                        stateHolder.safeIndex + 1,
                        stateHolder.questions.size
                    ),
                    fontSize = 13.sp,
                    color = colors.textSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = currentQuestion.question,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                lineHeight = 24.sp
            )

            if (currentQuestion.codeSnippet.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF090D16),
                    border = BorderStroke(1.dp, Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = currentQuestion.codeSnippet,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = Color(0xFF80CBC4),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            currentQuestion.options.forEachIndexed { idx, option ->
                val isSelected = selectedOption == idx
                val isCorrect = idx == currentQuestion.correctOptionIndex
                val showResult = selectedOption != null

                val bg = when {
                    !showResult -> colors.surfaceMuted
                    isCorrect -> colors.successContainer
                    isSelected && !isCorrect -> colors.errorContainer
                    else -> colors.surfaceMuted
                }

                val border = when {
                    !showResult -> colors.border
                    isCorrect -> colors.successBorder
                    isSelected && !isCorrect -> colors.errorBorder
                    else -> colors.border
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(enabled = selectedOption == null) {
                            stateHolder.selectAnswer(idx)
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = bg,
                    border = BorderStroke(1.dp, border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(colors.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stateHolder.optionLabels.getOrElse(idx) { "${idx + 1}" },
                                fontWeight = FontWeight.Bold,
                                color = colors.onPrimaryContainer,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = option,
                            fontSize = 14.sp,
                            color = colors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        if (showResult && (isCorrect || isSelected)) {
                            Icon(
                                imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                                contentDescription = null,
                                tint = if (isCorrect) colors.successBorder else colors.errorBorder,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            if (selectedOption != null && currentQuestion.explanation.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colors.warningContainer
                ) {
                    Text(
                        text = strings.formatExplanation(currentQuestion.explanation),
                        fontSize = 13.sp,
                        color = colors.warningText,
                        modifier = Modifier.padding(12.dp),
                        lineHeight = 18.sp
                    )
                }
            }

            Button(
                onClick = stateHolder::nextQuestion,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = if (colors.isDark) Color(0xFF00252A) else Color.White
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.nextQuestionButton, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

