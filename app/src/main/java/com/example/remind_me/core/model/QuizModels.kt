package com.example.remind_me.core.model

enum class StudyMode {
    QUIZ,
    MEMORY_CARD;

    companion object {
        fun fromName(value: String?): StudyMode {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: QUIZ
        }
    }
}

data class TopicItem(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val isEnabled: Boolean = true,
    val selectedChapterId: String = "",
    // Placeholder fields for toggling between Quiz and Memory Card per topic
    val supportedModes: List<StudyMode> = listOf(StudyMode.QUIZ, StudyMode.MEMORY_CARD),
    val preferredStudyMode: StudyMode = StudyMode.QUIZ
)

data class ChapterItem(
    val id: String,
    val topicId: String,
    val chapterNumber: Int,
    val title: String,
    val description: String = ""
)

data class QuizQuestion(
    val id: String,
    val topicId: String,
    val chapterId: String,
    val question: String,
    val codeSnippet: String = "",
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String = "",
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    // Placeholder fields for Memory Card (Flashcard) mode
    val cardFront: String = "",
    val cardBack: String = "",
    val cardHint: String = "",
    val memoryBoxLevel: Int = 1,
    val nextReviewAtMillis: Long = 0L
)

data class LockScreenQuizPayload(
    val question: QuizQuestion,
    val topic: TopicItem?,
    val chapter: ChapterItem?,
    val activeStudyMode: StudyMode = StudyMode.QUIZ
)

data class ReminderSettings(
    val isServiceEnabled: Boolean = false,
    val triggerEveryNUnlocks: Int = 1,
    val cooldownMinutes: Int = 0,
    val requireCorrectAnswerToDismiss: Boolean = true,
    val totalAnswered: Int = 0,
    val totalCorrect: Int = 0,
    val currentStreak: Int = 0,
    val languageCode: String = "vi",
    // Placeholder toggle between QUIZ and MEMORY_CARD
    val studyMode: StudyMode = StudyMode.QUIZ
)
