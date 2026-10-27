package com.example.remind_me.feature.topic_list.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.remind_me.core.localization.LocalAppStrings
import com.example.remind_me.core.localization.getAppStrings
import com.example.remind_me.core.storage.KnowledgeRepository
import com.example.remind_me.core.theme.RemindMeTheme
import com.example.remind_me.core.theme.RemindmeTheme
import com.example.remind_me.feature.lockscreen.service.LockScreenReminderService
import com.example.remind_me.feature.lockscreen.ui.LockScreenQuizActivity
import com.example.remind_me.feature.topic_detail.ui.TopicDetailScreen
import com.example.remind_me.feature.topic_list.components.*

@Composable
fun TopicListScreen() {
    val context = LocalContext.current
    val colors = RemindMeTheme.colors
    val repository = remember { KnowledgeRepository(context) }

    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    var topics by remember { mutableStateOf(repository.getTopics()) }
    var chapters by remember { mutableStateOf(repository.getChapters()) }
    var questions by remember { mutableStateOf(repository.getQuestions()) }
    var settings by remember { mutableStateOf(repository.getSettings()) }
    var hasOverlayPermission by remember {
        mutableStateOf(Settings.canDrawOverlays(context))
    }

    val appStrings = remember(settings.languageCode) {
        getAppStrings(settings.languageCode)
    }

    var showAddTopicDialog by remember { mutableStateOf(false) }
    var selectedTopicIdForDetail by remember { mutableStateOf<String?>(null) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        LockScreenReminderService.start(context)
    }

    fun refreshAllData() {
        topics = repository.getTopics()
        chapters = repository.getChapters()
        questions = repository.getQuestions()
        settings = repository.getSettings()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasOverlayPermission = Settings.canDrawOverlays(context)
                refreshAllData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        if (settings.isServiceEnabled) {
            LockScreenReminderService.start(context)
        }
    }

    val filteredTopics = remember(topics, chapters, questions, searchQuery) {
        if (searchQuery.isBlank()) {
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
    }

    val chaptersByTopic = remember(chapters) {
        chapters.groupBy { it.topicId }.mapValues { (_, list) -> list.sortedBy { it.chapterNumber } }
    }

    val questionCountByTopic = remember(questions) {
        questions.groupingBy { it.topicId }.eachCount()
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
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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

    val activeDetailTopic = topics.find { it.id == selectedTopicIdForDetail }

    CompositionLocalProvider(LocalAppStrings provides appStrings) {
        Scaffold(
            containerColor = colors.background,
            bottomBar = {
                TopicListBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = {
                        selectedTopicIdForDetail = null
                        selectedTab = it
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background)
                    .padding(innerPadding)
            ) {
                if (activeDetailTopic != null) {
                    val detailChapters = chaptersByTopic[activeDetailTopic.id].orEmpty()
                    val detailQuestions = questions.filter { it.topicId == activeDetailTopic.id }

                    TopicDetailScreen(
                        topic = activeDetailTopic,
                        chapters = detailChapters,
                        questions = detailQuestions,
                        onBack = { selectedTopicIdForDetail = null },
                        onToggleTopicEnabled = {
                            repository.toggleTopicEnabled(activeDetailTopic.id)
                            refreshAllData()
                        },
                        onChangeStudyMode = { mode ->
                            repository.updateTopicPreferredStudyMode(activeDetailTopic.id, mode)
                            refreshAllData()
                        },
                        onSelectChapterForLockScreen = { chapterId ->
                            repository.selectChapterForTopic(activeDetailTopic.id, chapterId)
                            refreshAllData()
                        },
                        onAddChapter = { title, description ->
                            repository.addChapter(activeDetailTopic.id, title, description)
                            refreshAllData()
                        },
                        onAddQuestionToChapter = { chapterId, questionText, options, correctIdx, explanation, cardFront, cardBack, cardHint ->
                            repository.addQuestion(
                                topicId = activeDetailTopic.id,
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
                        },
                        onDeleteQuestion = { questionId ->
                            repository.deleteQuestion(questionId)
                            refreshAllData()
                        },
                        onPreviewLockScreenForChapter = { chapterId ->
                            openLockScreenPreview(
                                topicId = activeDetailTopic.id,
                                chapterId = chapterId
                            )
                        }
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        TopicListHeader(
                            title = when (selectedTab) {
                                0 -> appStrings.appTitle
                                1 -> appStrings.tabTopics
                                2 -> appStrings.tabPractice
                                else -> appStrings.tabSettings
                            },
                            streak = settings.currentStreak,
                            onPreviewLockScreen = { openLockScreenPreview() }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        when (selectedTab) {
                            0 -> {
                                LearningStatsSummaryCard(
                                    settings = settings,
                                    totalQuestions = questions.size,
                                    activeTopicsCount = topics.count { it.isEnabled }
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                Text(
                                    text = appStrings.lockScreenStatusTitle,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                TopicListConnectedDevices(
                                    isServiceEnabled = settings.isServiceEnabled,
                                    hasOverlayPermission = hasOverlayPermission,
                                    triggerEveryNUnlocks = settings.triggerEveryNUnlocks,
                                    onToggleService = { toggleLockScreenService(it) },
                                    onRequestOverlayPermission = { requestOverlayPermission() },
                                    onPreviewLockScreen = { openLockScreenPreview() }
                                )

                                Spacer(modifier = Modifier.height(22.dp))

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = appStrings.activeTopicsSectionTitle,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = appStrings.activeTopicsSectionSubtitle,
                                        fontSize = 12.sp,
                                        color = colors.textSecondary
                                    )
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        TextButton(
                                            onClick = { selectedTab = 1 },
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                                        ) {
                                            Text(
                                                text = appStrings.seeAll,
                                                color = colors.primary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                KnowledgeTopicsGrid(
                                    topics = topics,
                                    chaptersByTopic = chaptersByTopic,
                                    questionCountByTopic = questionCountByTopic,
                                    onTopicClick = { selectedTopicIdForDetail = it.id },
                                    onToggleTopic = { topicId ->
                                        repository.toggleTopicEnabled(topicId)
                                        refreshAllData()
                                    }
                                )
                            }

                            1 -> {
                                TopicListSearchBar(
                                    query = searchQuery,
                                    onQueryChange = { searchQuery = it },
                                    placeholder = appStrings.searchPlaceholder
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${appStrings.tabTopics} (${filteredTopics.size})",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textPrimary
                                        )
                                        Text(
                                            text = appStrings.activeTopicsSectionSubtitle,
                                            fontSize = 12.sp,
                                            color = colors.textSecondary
                                        )
                                    }

                                    Button(
                                        onClick = { showAddTopicDialog = true },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = colors.primary,
                                            contentColor = if (colors.isDark) Color(0xFF00252A) else Color.White
                                        )
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(appStrings.addTopicButton, fontWeight = FontWeight.SemiBold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                KnowledgeTopicsGrid(
                                    topics = filteredTopics,
                                    chaptersByTopic = chaptersByTopic,
                                    questionCountByTopic = questionCountByTopic,
                                    onTopicClick = { selectedTopicIdForDetail = it.id },
                                    onToggleTopic = { topicId ->
                                        repository.toggleTopicEnabled(topicId)
                                        refreshAllData()
                                    }
                                )
                            }

                            2 -> {
                                InAppPracticeSection(
                                    questions = questions,
                                    topics = topics,
                                    onAnswerRecorded = { questionId, isCorrect ->
                                        repository.recordAnswer(questionId, isCorrect)
                                        refreshAllData()
                                    }
                                )
                            }

                            3 -> {
                                ReminderSettingsSection(
                                    settings = settings,
                                    onUpdateSettings = { updated ->
                                        repository.saveSettings(updated)
                                        settings = updated
                                    },
                                    onChangeLanguage = { langCode ->
                                        repository.syncContentForLanguagePreservingProgress(langCode)
                                        refreshAllData()
                                    },
                                    onResetSampleData = {
                                        repository.resetToDefaultData(settings.languageCode)
                                        refreshAllData()
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }
            }
        }
    }

    if (showAddTopicDialog) {
        AddTopicDialog(
            onDismiss = { showAddTopicDialog = false },
            onConfirm = { title, description ->
                val created = repository.addTopic(title, description)
                refreshAllData()
                showAddTopicDialog = false
                selectedTopicIdForDetail = created.id
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TopicListScreenPreview() {
    RemindmeTheme {
        TopicListScreen()
    }
}
