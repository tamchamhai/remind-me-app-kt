package com.example.remind_me.feature.main.logic

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import com.example.remind_me.core.localization.AppStrings
import com.example.remind_me.core.localization.getAppStrings
import com.example.remind_me.core.model.ChapterItem
import com.example.remind_me.core.model.QuizQuestion
import com.example.remind_me.core.model.ReminderSettings
import com.example.remind_me.core.model.StudyMode
import com.example.remind_me.core.model.TopicItem
import com.example.remind_me.core.storage.KnowledgeRepository
import com.example.remind_me.feature.lockscreen.service.LockScreenReminderService
import com.example.remind_me.feature.lockscreen.ui.LockScreenQuizActivity

@Stable
class MainStateHolder(
    private val context: Context,
    private val repository: KnowledgeRepository,
    private val onRequestNotificationPermission: () -> Unit
) {
    var selectedTab by mutableIntStateOf(0)
        private set

    var isSearchActive by mutableStateOf(false)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var topics by mutableStateOf(repository.getTopics())
        private set

    var chapters by mutableStateOf(repository.getChapters())
        private set

    var questions by mutableStateOf(repository.getQuestions())
        private set

    var settings by mutableStateOf(repository.getSettings())
        private set

    var hasOverlayPermission by mutableStateOf(Settings.canDrawOverlays(context))
        private set

    var showAddTopicDialog by mutableStateOf(false)
        private set

    var selectedTopicIdForDetail by mutableStateOf<String?>(null)
        private set

    val appStrings: AppStrings
        get() = getAppStrings(settings.languageCode)

    val activeDetailTopic: TopicItem?
        get() = topics.find { it.id == selectedTopicIdForDetail }

    val detailChapters: List<ChapterItem>
        get() {
            val topicId = activeDetailTopic?.id ?: return emptyList()
            return chapters.filter { it.topicId == topicId }.sortedBy { it.chapterNumber }
        }

    val detailQuestions: List<QuizQuestion>
        get() {
            val topicId = activeDetailTopic?.id ?: return emptyList()
            return questions.filter { it.topicId == topicId }
        }

    fun headerTitle(strings: AppStrings): String {
        return when (selectedTab) {
            0 -> strings.appTitle
            1 -> strings.tabTopics
            2 -> strings.tabPractice
            else -> strings.tabSettings
        }
    }

    fun refreshAllData() {
        hasOverlayPermission = Settings.canDrawOverlays(context)
        topics = repository.getTopics()
        chapters = repository.getChapters()
        questions = repository.getQuestions()
        settings = repository.getSettings()
    }

    fun ensureServiceStartedIfEnabled() {
        if (settings.isServiceEnabled) {
            LockScreenReminderService.start(context)
        }
    }

    fun selectTab(tabIndex: Int) {
        selectedTopicIdForDetail = null
        selectedTab = tabIndex
    }

    fun toggleSearch() {
        isSearchActive = !isSearchActive
        if (!isSearchActive) {
            searchQuery = ""
        } else if (selectedTab != 0 && selectedTab != 1) {
            selectedTab = 1
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun openTopicDetail(topic: TopicItem) {
        selectedTopicIdForDetail = topic.id
    }

    fun closeTopicDetail() {
        selectedTopicIdForDetail = null
    }

    fun toggleTopicEnabled(topicId: String) {
        repository.toggleTopicEnabled(topicId)
        refreshAllData()
    }

    fun updateTopicStudyMode(topicId: String, mode: StudyMode) {
        repository.updateTopicPreferredStudyMode(topicId, mode)
        refreshAllData()
    }

    fun selectChapterForTopic(topicId: String, chapterId: String) {
        repository.selectChapterForTopic(topicId, chapterId)
        refreshAllData()
    }

    fun addChapterToTopic(topicId: String, title: String, description: String) {
        repository.addChapter(topicId, title, description)
        refreshAllData()
    }

    fun addQuestionToChapter(
        topicId: String,
        chapterId: String,
        questionText: String,
        options: List<String>,
        correctIdx: Int,
        explanation: String,
        cardFront: String,
        cardBack: String,
        cardHint: String
    ) {
        repository.addQuestion(
            topicId = topicId,
            chapterId = chapterId,
            question = questionText,
            options = options,
            correctOptionIndex = correctIdx,
            explanation = explanation,
            cardFront = cardFront,
            cardBack = cardBack,
            cardHint = cardHint
        )
        refreshAllData()
    }

    fun deleteQuestion(questionId: String) {
        repository.deleteQuestion(questionId)
        refreshAllData()
    }

    fun openAddTopicDialog() {
        showAddTopicDialog = true
    }

    fun closeAddTopicDialog() {
        showAddTopicDialog = false
    }

    fun confirmAddTopic(title: String, description: String) {
        val created = repository.addTopic(title, description)
        refreshAllData()
        showAddTopicDialog = false
        selectedTopicIdForDetail = created.id
    }

    fun recordPracticeAnswer(questionId: String, isCorrect: Boolean) {
        repository.recordAnswer(questionId, isCorrect)
        refreshAllData()
    }

    fun updateSettings(updated: ReminderSettings) {
        repository.saveSettings(updated)
        settings = updated
    }

    fun changeLanguage(languageCode: String) {
        repository.syncContentForLanguagePreservingProgress(languageCode)
        refreshAllData()
    }

    fun resetSampleData() {
        repository.resetToDefaultData(settings.languageCode)
        refreshAllData()
    }

    fun toggleLockScreenService(enable: Boolean) {
        val updated = settings.copy(isServiceEnabled = enable)
        repository.saveSettings(updated)
        settings = updated
        if (enable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                onRequestNotificationPermission()
            } else {
                LockScreenReminderService.start(context)
            }
        } else {
            LockScreenReminderService.stop(context)
        }
    }

    fun openLockScreenPreview(topicId: String? = null, chapterId: String? = null) {
        context.startActivity(
            LockScreenQuizActivity.createIntent(
                context = context,
                topicId = topicId,
                chapterId = chapterId
            )
        )
    }

    fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        )
        runCatching { context.startActivity(intent) }
    }
}

