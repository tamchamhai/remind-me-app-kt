package com.example.remind_me.feature.topic_list.logic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.example.remind_me.core.model.ChapterItem
import com.example.remind_me.core.model.QuizQuestion
import com.example.remind_me.core.model.ReminderSettings
import com.example.remind_me.core.model.TopicItem

@Stable
class TopicListStateHolder(
    val topics: List<TopicItem>,
    val chapters: List<ChapterItem>,
    val questions: List<QuizQuestion>,
    val settings: ReminderSettings,
    val hasOverlayPermission: Boolean,
    val searchQuery: String,
    val onSearchQueryChange: (String) -> Unit,
    val onTopicClick: (TopicItem) -> Unit,
    val onToggleTopic: (String) -> Unit,
    val onOpenAddTopicDialog: () -> Unit,
    val onSeeAllTopics: () -> Unit,
    val onToggleService: (Boolean) -> Unit,
    val onRequestOverlayPermission: () -> Unit,
    val onPreviewLockScreen: () -> Unit
) {
    val filteredTopics: List<TopicItem> = if (searchQuery.isBlank()) {
        topics
    } else {
        val q = searchQuery.trim().lowercase()
        val matchingTopicIdsFromChapters = chapters
            .filter { it.title.lowercase().contains(q) || it.description.lowercase().contains(q) }
            .map { it.topicId }
            .toSet()
        val matchingTopicIdsFromQuestions = questions
            .filter { it.question.lowercase().contains(q) }
            .map { it.topicId }
            .toSet()
        topics.filter {
            it.title.lowercase().contains(q) ||
                it.description.lowercase().contains(q) ||
                it.id in matchingTopicIdsFromChapters ||
                it.id in matchingTopicIdsFromQuestions
        }
    }

    val chaptersByTopic: Map<String, List<ChapterItem>> =
        chapters.groupBy { it.topicId }.mapValues { (_, list) -> list.sortedBy { it.chapterNumber } }

    val questionCountByTopic: Map<String, Int> =
        questions.groupingBy { it.topicId }.eachCount()

    val totalQuestionsCount: Int
        get() = questions.size

    val activeTopicsCount: Int
        get() = topics.count { it.isEnabled }
}

@Composable
fun rememberTopicListStateHolder(
    topics: List<TopicItem>,
    chapters: List<ChapterItem>,
    questions: List<QuizQuestion>,
    settings: ReminderSettings,
    hasOverlayPermission: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onTopicClick: (TopicItem) -> Unit,
    onToggleTopic: (String) -> Unit,
    onOpenAddTopicDialog: () -> Unit,
    onSeeAllTopics: () -> Unit,
    onToggleService: (Boolean) -> Unit,
    onRequestOverlayPermission: () -> Unit,
    onPreviewLockScreen: () -> Unit
): TopicListStateHolder {
    return remember(topics, chapters, questions, settings, hasOverlayPermission, searchQuery) {
        TopicListStateHolder(
            topics = topics,
            chapters = chapters,
            questions = questions,
            settings = settings,
            hasOverlayPermission = hasOverlayPermission,
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onTopicClick = onTopicClick,
            onToggleTopic = onToggleTopic,
            onOpenAddTopicDialog = onOpenAddTopicDialog,
            onSeeAllTopics = onSeeAllTopics,
            onToggleService = onToggleService,
            onRequestOverlayPermission = onRequestOverlayPermission,
            onPreviewLockScreen = onPreviewLockScreen
        )
    }
}

