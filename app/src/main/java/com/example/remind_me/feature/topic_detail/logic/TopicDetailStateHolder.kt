package com.example.remind_me.feature.topic_detail.logic

import androidx.compose.runtime.*
import com.example.remind_me.core.model.ChapterItem
import com.example.remind_me.core.model.QuizQuestion
import com.example.remind_me.core.model.StudyMode
import com.example.remind_me.core.model.TopicItem

@Stable
class TopicDetailStateHolder(
    val topic: TopicItem,
    val chapters: List<ChapterItem>,
    val questions: List<QuizQuestion>,
    val onBack: () -> Unit,
    val onToggleTopicEnabled: () -> Unit,
    val onChangeStudyMode: (StudyMode) -> Unit,
    val onSelectChapterForLockScreen: (String) -> Unit,
    private val onAddChapter: (title: String, description: String) -> Unit,
    private val onAddQuestionToChapter: (
        chapterId: String,
        question: String,
        options: List<String>,
        correctIndex: Int,
        explanation: String,
        cardFront: String,
        cardBack: String,
        cardHint: String
    ) -> Unit,
    val onDeleteQuestion: (String) -> Unit,
    val onPreviewLockScreenForChapter: (chapterId: String) -> Unit
) {
    var expandedChapterId by mutableStateOf<String?>(
        topic.selectedChapterId.ifBlank { chapters.firstOrNull()?.id }
    )
        private set

    var showAddChapterDialog by mutableStateOf(false)
        private set

    var targetChapterForNewQuestion by mutableStateOf<ChapterItem?>(null)
        private set

    val activeChapter: ChapterItem?
        get() = chapters.find { it.id == topic.selectedChapterId } ?: chapters.firstOrNull()

    fun questionsForChapter(chapterId: String): List<QuizQuestion> {
        return questions.filter { it.chapterId == chapterId }
    }

    fun isChapterSelectedForLockScreen(chapterId: String): Boolean {
        return chapterId == (activeChapter?.id ?: "")
    }

    fun isChapterExpanded(chapterId: String): Boolean {
        return expandedChapterId == chapterId
    }

    fun toggleChapterExpand(chapterId: String) {
        expandedChapterId = if (expandedChapterId == chapterId) null else chapterId
    }

    fun openAddChapterDialog() {
        showAddChapterDialog = true
    }

    fun closeAddChapterDialog() {
        showAddChapterDialog = false
    }

    fun confirmAddChapter(title: String, description: String) {
        onAddChapter(title, description)
        showAddChapterDialog = false
    }

    fun openAddQuestionDialog(chapter: ChapterItem) {
        targetChapterForNewQuestion = chapter
    }

    fun closeAddQuestionDialog() {
        targetChapterForNewQuestion = null
    }

    fun confirmAddQuestion(
        questionText: String,
        options: List<String>,
        correctIndex: Int,
        explanation: String,
        cardFront: String,
        cardBack: String,
        cardHint: String
    ) {
        val chapter = targetChapterForNewQuestion ?: return
        onAddQuestionToChapter(
            chapter.id,
            questionText,
            options,
            correctIndex,
            explanation,
            cardFront,
            cardBack,
            cardHint
        )
        targetChapterForNewQuestion = null
    }
}

@Composable
fun rememberTopicDetailStateHolder(
    topic: TopicItem,
    chapters: List<ChapterItem>,
    questions: List<QuizQuestion>,
    onBack: () -> Unit,
    onToggleTopicEnabled: () -> Unit,
    onChangeStudyMode: (StudyMode) -> Unit,
    onSelectChapterForLockScreen: (String) -> Unit,
    onAddChapter: (title: String, description: String) -> Unit,
    onAddQuestionToChapter: (
        chapterId: String,
        question: String,
        options: List<String>,
        correctIndex: Int,
        explanation: String,
        cardFront: String,
        cardBack: String,
        cardHint: String
    ) -> Unit,
    onDeleteQuestion: (String) -> Unit,
    onPreviewLockScreenForChapter: (chapterId: String) -> Unit
): TopicDetailStateHolder {
    return remember(topic, chapters, questions) {
        TopicDetailStateHolder(
            topic = topic,
            chapters = chapters,
            questions = questions,
            onBack = onBack,
            onToggleTopicEnabled = onToggleTopicEnabled,
            onChangeStudyMode = onChangeStudyMode,
            onSelectChapterForLockScreen = onSelectChapterForLockScreen,
            onAddChapter = onAddChapter,
            onAddQuestionToChapter = onAddQuestionToChapter,
            onDeleteQuestion = onDeleteQuestion,
            onPreviewLockScreenForChapter = onPreviewLockScreenForChapter
        )
    }
}

