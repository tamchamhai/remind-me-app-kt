package com.example.remind_me.feature.lockscreen.logic

import androidx.compose.runtime.*
import com.example.remind_me.core.model.ChapterItem
import com.example.remind_me.core.model.QuizQuestion
import com.example.remind_me.core.model.TopicItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Stable
class LockScreenQuizStateHolder(
    val question: QuizQuestion,
    val topic: TopicItem?,
    val chapter: ChapterItem?,
    val streak: Int,
    val requireCorrectToDismiss: Boolean,
    val languageCode: String,
    private val onAnswerSubmitted: (Boolean) -> Unit,
    val onNextQuestion: () -> Unit,
    val onDismissLockScreen: () -> Unit
) {
    val optionLabels: List<String> = listOf("A", "B", "C", "D")

    var selectedOptionIndex by mutableStateOf<Int?>(null)
        private set

    var hasRecordedResult by mutableStateOf(false)
        private set

    var unlockedByCorrectAnswer by mutableStateOf(false)
        private set

    var currentTime by mutableStateOf(formatTime())
        private set

    var currentDate by mutableStateOf(formatDate(languageCode))
        private set

    val canDismiss: Boolean
        get() = !requireCorrectToDismiss || unlockedByCorrectAnswer

    val isCurrentSelectionCorrect: Boolean
        get() = selectedOptionIndex == question.correctOptionIndex

    fun refreshClock() {
        currentTime = formatTime()
        currentDate = formatDate(languageCode)
    }

    fun selectOption(index: Int) {
        if (unlockedByCorrectAnswer) return
        selectedOptionIndex = index
        val isCorrect = index == question.correctOptionIndex
        if (!hasRecordedResult) {
            hasRecordedResult = true
            onAnswerSubmitted(isCorrect)
        }
        if (isCorrect) {
            unlockedByCorrectAnswer = true
        }
    }

    companion object {
        fun formatTime(): String {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date())
        }

        fun formatDate(languageCode: String): String {
            val normalized = languageCode.lowercase()
            val locale = when (normalized) {
                "en" -> Locale.ENGLISH
                "ja" -> Locale.JAPANESE
                "zh-cn" -> Locale.SIMPLIFIED_CHINESE
                else -> Locale.forLanguageTag("vi-VN")
            }
            val pattern = if (normalized == "ja" || normalized == "zh-cn") {
                "yyyy年MM月dd日 (EEEE)"
            } else {
                "EEEE, dd/MM/yyyy"
            }
            val sdf = SimpleDateFormat(pattern, locale)
            return sdf.format(Date()).replaceFirstChar { it.uppercase() }
        }
    }
}

@Composable
fun rememberLockScreenQuizStateHolder(
    question: QuizQuestion,
    topic: TopicItem?,
    chapter: ChapterItem?,
    streak: Int,
    requireCorrectToDismiss: Boolean,
    languageCode: String,
    onAnswerSubmitted: (Boolean) -> Unit,
    onNextQuestion: () -> Unit,
    onDismissLockScreen: () -> Unit
): LockScreenQuizStateHolder {
    return remember(question.id, languageCode, streak, requireCorrectToDismiss) {
        LockScreenQuizStateHolder(
            question = question,
            topic = topic,
            chapter = chapter,
            streak = streak,
            requireCorrectToDismiss = requireCorrectToDismiss,
            languageCode = languageCode,
            onAnswerSubmitted = onAnswerSubmitted,
            onNextQuestion = onNextQuestion,
            onDismissLockScreen = onDismissLockScreen
        )
    }
}

