package com.example.remind_me.feature.practice.logic

import androidx.compose.runtime.*
import com.example.remind_me.core.model.QuizQuestion
import com.example.remind_me.core.model.TopicItem

@Stable
class PracticeStateHolder(
    val questions: List<QuizQuestion>,
    val topics: List<TopicItem>,
    private val onAnswerRecorded: (String, Boolean) -> Unit
) {
    val optionLabels: List<String> = listOf("A", "B", "C", "D")

    var currentIndex by mutableIntStateOf(0)
        private set

    var selectedOption by mutableStateOf<Int?>(null)
        private set

    val isEmpty: Boolean
        get() = questions.isEmpty()

    val safeIndex: Int
        get() = if (questions.isEmpty()) 0 else currentIndex % questions.size

    val currentQuestion: QuizQuestion?
        get() = if (questions.isEmpty()) null else questions[safeIndex]

    fun resolveTopicTitle(defaultFallback: String): String {
        val q = currentQuestion ?: return defaultFallback
        return topics.find { it.id == q.topicId }?.title ?: defaultFallback
    }

    fun selectAnswer(optionIndex: Int) {
        val q = currentQuestion ?: return
        if (selectedOption != null) return
        selectedOption = optionIndex
        val isCorrect = optionIndex == q.correctOptionIndex
        onAnswerRecorded(q.id, isCorrect)
    }

    fun nextQuestion() {
        if (questions.isEmpty()) return
        currentIndex = (currentIndex + 1) % questions.size
        selectedOption = null
    }
}

@Composable
fun rememberPracticeStateHolder(
    questions: List<QuizQuestion>,
    topics: List<TopicItem>,
    onAnswerRecorded: (String, Boolean) -> Unit
): PracticeStateHolder {
    return remember(questions, topics) {
        PracticeStateHolder(
            questions = questions,
            topics = topics,
            onAnswerRecorded = onAnswerRecorded
        )
    }
}

