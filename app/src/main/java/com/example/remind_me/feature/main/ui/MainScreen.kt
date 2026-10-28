package com.example.remind_me.feature.main.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.remind_me.core.localization.LocalAppStrings
import com.example.remind_me.core.storage.KnowledgeRepository
import com.example.remind_me.core.theme.RemindMeTheme
import com.example.remind_me.core.theme.RemindmeTheme
import com.example.remind_me.feature.lockscreen.service.LockScreenReminderService
import com.example.remind_me.feature.main.components.MainBottomBar
import com.example.remind_me.feature.main.components.MainHeader
import com.example.remind_me.feature.main.logic.MainStateHolder
import com.example.remind_me.feature.practice.ui.PracticeScreen
import com.example.remind_me.feature.settings.ui.SettingsScreen
import com.example.remind_me.feature.topic_detail.ui.TopicDetailScreen
import com.example.remind_me.feature.topic_list.components.AddTopicDialog
import com.example.remind_me.feature.topic_list.logic.rememberTopicListStateHolder
import com.example.remind_me.feature.topic_list.ui.OverviewTabScreen
import com.example.remind_me.feature.topic_list.ui.TopicListScreen

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val repository = remember { KnowledgeRepository(context) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        LockScreenReminderService.start(context)
    }

    val stateHolder = remember {
        MainStateHolder(
            context = context,
            repository = repository,
            onRequestNotificationPermission = {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                stateHolder.refreshAllData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        stateHolder.ensureServiceStartedIfEnabled()
    }

    MainContent(stateHolder = stateHolder)
}

@Composable
fun MainContent(
    stateHolder: MainStateHolder
) {
    val colors = RemindMeTheme.colors
    val appStrings = stateHolder.appStrings
    val activeDetailTopic = stateHolder.activeDetailTopic

    val topicListStateHolder = rememberTopicListStateHolder(
        topics = stateHolder.topics,
        chapters = stateHolder.chapters,
        questions = stateHolder.questions,
        settings = stateHolder.settings,
        hasOverlayPermission = stateHolder.hasOverlayPermission,
        searchQuery = stateHolder.searchQuery,
        onSearchQueryChange = stateHolder::updateSearchQuery,
        onTopicClick = stateHolder::openTopicDetail,
        onToggleTopic = stateHolder::toggleTopicEnabled,
        onOpenAddTopicDialog = stateHolder::openAddTopicDialog,
        onSeeAllTopics = { stateHolder.selectTab(1) },
        onToggleService = stateHolder::toggleLockScreenService,
        onRequestOverlayPermission = stateHolder::requestOverlayPermission,
        onPreviewLockScreen = { stateHolder.openLockScreenPreview() }
    )

    CompositionLocalProvider(LocalAppStrings provides appStrings) {
        Scaffold(
            containerColor = colors.background,
            topBar = {
                if (activeDetailTopic == null) {
                    MainHeader(
                        title = stateHolder.headerTitle(appStrings),
                        streak = stateHolder.settings.currentStreak,
                        isSearchActive = stateHolder.isSearchActive,
                        searchQuery = stateHolder.searchQuery,
                        onToggleSearch = stateHolder::toggleSearch,
                        onSearchQueryChange = stateHolder::updateSearchQuery
                    )
                }
            },
            bottomBar = {
                MainBottomBar(
                    selectedTab = stateHolder.selectedTab,
                    onTabSelected = stateHolder::selectTab
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
                    TopicDetailScreen(
                        topic = activeDetailTopic,
                        chapters = stateHolder.detailChapters,
                        questions = stateHolder.detailQuestions,
                        onBack = stateHolder::closeTopicDetail,
                        onToggleTopicEnabled = {
                            stateHolder.toggleTopicEnabled(activeDetailTopic.id)
                        },
                        onChangeStudyMode = { mode ->
                            stateHolder.updateTopicStudyMode(activeDetailTopic.id, mode)
                        },
                        onSelectChapterForLockScreen = { chapterId ->
                            stateHolder.selectChapterForTopic(activeDetailTopic.id, chapterId)
                        },
                        onAddChapter = { title, description ->
                            stateHolder.addChapterToTopic(activeDetailTopic.id, title, description)
                        },
                        onAddQuestionToChapter = { chapterId, questionText, options, correctIdx, explanation, cardFront, cardBack, cardHint ->
                            stateHolder.addQuestionToChapter(
                                topicId = activeDetailTopic.id,
                                chapterId = chapterId,
                                questionText = questionText,
                                options = options,
                                correctIdx = correctIdx,
                                explanation = explanation,
                                cardFront = cardFront,
                                cardBack = cardBack,
                                cardHint = cardHint
                            )
                        },
                        onDeleteQuestion = stateHolder::deleteQuestion,
                        onPreviewLockScreenForChapter = { chapterId ->
                            stateHolder.openLockScreenPreview(
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
                        when (stateHolder.selectedTab) {
                            0 -> {
                                OverviewTabScreen(stateHolder = topicListStateHolder)
                            }
                            1 -> {
                                TopicListScreen(stateHolder = topicListStateHolder)
                            }
                            2 -> {
                                PracticeScreen(
                                    questions = stateHolder.questions,
                                    topics = stateHolder.topics,
                                    onAnswerRecorded = stateHolder::recordPracticeAnswer
                                )
                            }
                            3 -> {
                                SettingsScreen(
                                    settings = stateHolder.settings,
                                    onUpdateSettings = stateHolder::updateSettings,
                                    onChangeLanguage = stateHolder::changeLanguage,
                                    onResetSampleData = stateHolder::resetSampleData
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }
            }
        }
    }

    if (stateHolder.showAddTopicDialog) {
        AddTopicDialog(
            onDismiss = stateHolder::closeAddTopicDialog,
            onConfirm = stateHolder::confirmAddTopic
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    RemindmeTheme {
        MainScreen()
    }
}

