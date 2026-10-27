package com.example.remind_me.feature.topic_list.components

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remind_me.core.localization.AppLanguage
import com.example.remind_me.core.model.QuizQuestion
import com.example.remind_me.core.model.ReminderSettings
import com.example.remind_me.core.model.StudyMode
import com.example.remind_me.core.model.TopicItem
import com.example.remind_me.core.theme.RemindMeTheme

@Composable
fun LearningStatsSummaryCard(
    settings: ReminderSettings,
    totalQuestions: Int,
    activeTopicsCount: Int,
    modifier: Modifier = Modifier
) {
    val colors = RemindMeTheme.colors
    val strings = RemindMeTheme.strings
    val accuracy = if (settings.totalAnswered > 0) {
        (settings.totalCorrect * 100) / settings.totalAnswered
    } else {
        0
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = colors.bannerBackground,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatColumnItem(value = "${settings.totalAnswered}", label = strings.statsAnswered)
            VerticalDivider(
                modifier = Modifier.height(36.dp),
                color = Color.White.copy(alpha = 0.25f)
            )
            StatColumnItem(value = "$accuracy%", label = strings.statsAccuracy)
            VerticalDivider(
                modifier = Modifier.height(36.dp),
                color = Color.White.copy(alpha = 0.25f)
            )
            StatColumnItem(value = "$totalQuestions", label = strings.statsTotalQuestions)
            VerticalDivider(
                modifier = Modifier.height(36.dp),
                color = Color.White.copy(alpha = 0.25f)
            )
            StatColumnItem(value = "$activeTopicsCount", label = strings.statsActiveTopics)
        }
    }
}

@Composable
private fun StatColumnItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.85f)
        )
    }
}

@Composable
fun InAppPracticeSection(
    questions: List<QuizQuestion>,
    topics: List<TopicItem>,
    onAnswerRecorded: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RemindMeTheme.colors
    val strings = RemindMeTheme.strings

    if (questions.isEmpty()) {
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

    var currentIndex by remember { mutableIntStateOf(0) }
    val safeIndex = currentIndex % questions.size
    val currentQuestion = questions[safeIndex]
    val topicTitle = topics.find { it.id == currentQuestion.topicId }?.title ?: strings.tabTopics

    var selectedOption by remember(currentQuestion.id, currentIndex) { mutableStateOf<Int?>(null) }
    val optionLabels = listOf("A", "B", "C", "D")

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
                    text = strings.formatPracticeQuestionCounter(safeIndex + 1, questions.size),
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
                            selectedOption = idx
                            onAnswerRecorded(currentQuestion.id, isCorrect)
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
                                text = optionLabels.getOrElse(idx) { "${idx + 1}" },
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
                onClick = { currentIndex = (currentIndex + 1) % questions.size },
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

@Composable
fun ReminderSettingsSection(
    settings: ReminderSettings,
    onUpdateSettings: (ReminderSettings) -> Unit,
    onChangeLanguage: (String) -> Unit,
    onResetSampleData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RemindMeTheme.colors
    val strings = RemindMeTheme.strings
    val unlockOptions = listOf(1, 2, 3, 5)
    val cooldownOptions = listOf(0, 5, 15, 30)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Study Mode Placeholder Toggle (Quiz vs Memory Card)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.border),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = strings.studyModeTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = strings.studyModeSubtitle,
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = settings.studyMode == StudyMode.QUIZ,
                        onClick = {
                            onUpdateSettings(settings.copy(studyMode = StudyMode.QUIZ))
                        },
                        label = { Text(strings.studyModeQuiz) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = settings.studyMode == StudyMode.MEMORY_CARD,
                        onClick = {
                            onUpdateSettings(settings.copy(studyMode = StudyMode.MEMORY_CARD))
                        },
                        label = { Text(strings.studyModeMemoryCard) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (settings.studyMode == StudyMode.MEMORY_CARD) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = colors.warningContainer
                    ) {
                        Text(
                            text = strings.studyModePlaceholderHint,
                            fontSize = 12.sp,
                            color = colors.warningText,
                            modifier = Modifier.padding(10.dp),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // 2. Language Selector (UI Local + Server Mock API Data Language)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.border),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = strings.languageTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = strings.languageSubtitle,
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppLanguage.entries.forEach { lang ->
                        FilterChip(
                            selected = settings.languageCode == lang.code,
                            onClick = {
                                if (settings.languageCode != lang.code) {
                                    onChangeLanguage(lang.code)
                                }
                            },
                            label = { Text(lang.displayName) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 3. Unlock Frequency
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.border),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = strings.unlockFrequencyTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = strings.unlockFrequencySubtitle,
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    unlockOptions.forEach { count ->
                        val selected = settings.triggerEveryNUnlocks == count
                        FilterChip(
                            selected = selected,
                            onClick = {
                                onUpdateSettings(settings.copy(triggerEveryNUnlocks = count))
                            },
                            label = { Text(strings.formatEveryNUnlocks(count)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 4. Cooldown
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.border),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = strings.cooldownTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = strings.cooldownSubtitle,
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cooldownOptions.forEach { mins ->
                        val selected = settings.cooldownMinutes == mins
                        FilterChip(
                            selected = selected,
                            onClick = {
                                onUpdateSettings(settings.copy(cooldownMinutes = mins))
                            },
                            label = { Text(strings.formatCooldownOption(mins)) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 5. Require Correct Answer
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.border),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.requireCorrectTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = strings.requireCorrectSubtitle,
                        fontSize = 13.sp,
                        color = colors.textSecondary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Switch(
                    checked = settings.requireCorrectAnswerToDismiss,
                    onCheckedChange = {
                        onUpdateSettings(settings.copy(requireCorrectAnswerToDismiss = it))
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = colors.primary
                    )
                )
            }
        }

        OutlinedButton(
            onClick = onResetSampleData,
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, colors.border),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = colors.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(strings.resetSampleDataButton, color = colors.primary)
        }
    }
}

@Composable
fun AddTopicDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String) -> Unit
) {
    val colors = RemindMeTheme.colors
    val strings = RemindMeTheme.strings
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        titleContentColor = colors.textPrimary,
        textContentColor = colors.textPrimary,
        title = { Text(strings.addTopicDialogTitle, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(strings.addTopicNameLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(strings.addTopicDescLabel) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, description)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
            ) {
                Text(strings.saveTopicButton)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancelButton, color = colors.textSecondary)
            }
        }
    )
}
