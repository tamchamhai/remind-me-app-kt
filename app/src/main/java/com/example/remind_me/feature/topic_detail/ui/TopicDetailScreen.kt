package com.example.remind_me.feature.topic_detail.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remind_me.core.model.ChapterItem
import com.example.remind_me.core.model.QuizQuestion
import com.example.remind_me.core.model.StudyMode
import com.example.remind_me.core.model.TopicItem
import com.example.remind_me.core.theme.RemindMeTheme
import com.example.remind_me.feature.settings.components.SettingsDropdownSelector
import com.example.remind_me.feature.topic_detail.components.AddChapterDialog
import com.example.remind_me.feature.topic_detail.components.AddChapterQuestionDialog
import com.example.remind_me.feature.topic_detail.components.ChapterSelectorCard
import com.example.remind_me.feature.topic_detail.logic.TopicDetailStateHolder
import com.example.remind_me.feature.topic_detail.logic.rememberTopicDetailStateHolder

@Composable
fun TopicDetailScreen(
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
) {
    val stateHolder = rememberTopicDetailStateHolder(
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
    TopicDetailContent(stateHolder = stateHolder)
}

@Composable
fun TopicDetailContent(
    stateHolder: TopicDetailStateHolder
) {
    BackHandler(onBack = stateHolder.onBack)

    val colors = RemindMeTheme.colors
    val strings = RemindMeTheme.strings
    val topic = stateHolder.topic
    val chapters = stateHolder.chapters
    val activeChapter = stateHolder.activeChapter

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Fixed Header containing ONLY Back Button and Toggle Switch
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colors.surface,
            shadowElevation = 2.dp
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        onClick = stateHolder.onBack,
                        shape = CircleShape,
                        color = colors.surfaceMuted,
                        border = BorderStroke(1.dp, colors.border),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = strings.backButton,
                                tint = colors.textPrimary
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (topic.isEnabled) strings.enabledOnLockScreen else strings.disabledOnLockScreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (topic.isEnabled) colors.primary else colors.textSecondary
                        )
                        Switch(
                            checked = topic.isEnabled,
                            onCheckedChange = { stateHolder.onToggleTopicEnabled() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = colors.primary
                            )
                        )
                    }
                }
                HorizontalDivider(color = colors.border, thickness = 0.5.dp)
            }
        }

        // Scrollable Main Content containing Topic Title, Description, Study Mode Toggle, Active Chapter & Chapters List
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = topic.title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                lineHeight = 32.sp
            )
            if (topic.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = topic.description,
                    fontSize = 14.sp,
                    color = colors.textSecondary,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Study Mode Dropdown per Topic
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = colors.surface,
                border = BorderStroke(1.dp, colors.border)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = strings.studyModeTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingsDropdownSelector(
                        options = listOf(StudyMode.QUIZ, StudyMode.MEMORY_CARD),
                        selectedOption = topic.preferredStudyMode,
                        optionLabel = { mode ->
                            when (mode) {
                                StudyMode.QUIZ -> strings.studyModeQuiz
                                StudyMode.MEMORY_CARD -> strings.studyModeMemoryCard
                            }
                        },
                        onOptionSelected = stateHolder.onChangeStudyMode
                    )
                    if (topic.preferredStudyMode == StudyMode.MEMORY_CARD) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = strings.studyModePlaceholderHint,
                            fontSize = 11.sp,
                            color = colors.warningText,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active Chapter Summary Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = colors.bannerBackground,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = strings.activeChapterBannerTitle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = activeChapter?.title ?: strings.noChapterSelected,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = strings.activeChapterBannerDesc,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 17.sp
                    )

                    if (activeChapter != null) {
                        Button(
                            onClick = { stateHolder.onPreviewLockScreenForChapter(activeChapter.id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF00838F)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.previewLockScreenChapter,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Section Header: Chapters List
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${strings.chapterListTitle} (${chapters.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = strings.chapterListSubtitle,
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = stateHolder::openAddChapterDialog,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, colors.border),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = colors.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(strings.addChapterButton, fontSize = 12.sp, color = colors.primary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                chapters.forEach { chapter ->
                    val chapterQuestions = stateHolder.questionsForChapter(chapter.id)
                    val isSelected = stateHolder.isChapterSelectedForLockScreen(chapter.id)
                    val isExpanded = stateHolder.isChapterExpanded(chapter.id)

                    ChapterSelectorCard(
                        chapter = chapter,
                        questions = chapterQuestions,
                        isSelectedForLockScreen = isSelected,
                        isExpanded = isExpanded,
                        onSelectForLockScreen = {
                            stateHolder.onSelectChapterForLockScreen(chapter.id)
                        },
                        onToggleExpand = {
                            stateHolder.toggleChapterExpand(chapter.id)
                        },
                        onAddQuestionToChapter = {
                            stateHolder.openAddQuestionDialog(chapter)
                        },
                        onDeleteQuestion = stateHolder.onDeleteQuestion,
                        onPreviewChapterOnLockScreen = {
                            stateHolder.onPreviewLockScreenForChapter(chapter.id)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    if (stateHolder.showAddChapterDialog) {
        AddChapterDialog(
            nextChapterNumber = chapters.size + 1,
            onDismiss = stateHolder::closeAddChapterDialog,
            onConfirm = stateHolder::confirmAddChapter
        )
    }

    val chapterForNewQ = stateHolder.targetChapterForNewQuestion
    if (chapterForNewQ != null) {
        AddChapterQuestionDialog(
            chapter = chapterForNewQ,
            onDismiss = stateHolder::closeAddQuestionDialog,
            onConfirm = stateHolder::confirmAddQuestion
        )
    }
}
