package com.example.remind_me.feature.topic_list.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remind_me.core.theme.RemindMeTheme
import com.example.remind_me.feature.topic_list.components.*
import com.example.remind_me.feature.topic_list.logic.TopicListStateHolder

@Composable
fun OverviewTabScreen(
    stateHolder: TopicListStateHolder,
    modifier: Modifier = Modifier
) {
    val colors = RemindMeTheme.colors
    val strings = RemindMeTheme.strings

    Column(modifier = modifier.fillMaxWidth()) {
        LearningStatsSummaryCard(
            settings = stateHolder.settings,
            totalQuestions = stateHolder.totalQuestionsCount,
            activeTopicsCount = stateHolder.activeTopicsCount
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = strings.lockScreenStatusTitle,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        TopicListConnectedDevices(
            isServiceEnabled = stateHolder.settings.isServiceEnabled,
            hasOverlayPermission = stateHolder.hasOverlayPermission,
            triggerEveryNUnlocks = stateHolder.settings.triggerEveryNUnlocks,
            onToggleService = stateHolder.onToggleService,
            onRequestOverlayPermission = stateHolder.onRequestOverlayPermission,
            onPreviewLockScreen = stateHolder.onPreviewLockScreen
        )

        Spacer(modifier = Modifier.height(22.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = strings.activeTopicsSectionTitle,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                text = strings.activeTopicsSectionSubtitle,
                fontSize = 12.sp,
                color = colors.textSecondary
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(
                    onClick = stateHolder.onSeeAllTopics,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = strings.seeAll,
                        color = colors.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        KnowledgeTopicsGrid(
            topics = stateHolder.filteredTopics,
            chaptersByTopic = stateHolder.chaptersByTopic,
            questionCountByTopic = stateHolder.questionCountByTopic,
            onTopicClick = stateHolder.onTopicClick,
            onToggleTopic = stateHolder.onToggleTopic
        )
    }
}

@Composable
fun TopicListScreen(
    stateHolder: TopicListStateHolder,
    modifier: Modifier = Modifier
) {
    val colors = RemindMeTheme.colors
    val strings = RemindMeTheme.strings

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${strings.tabTopics} (${stateHolder.filteredTopics.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = strings.activeTopicsSectionSubtitle,
                    fontSize = 12.sp,
                    color = colors.textSecondary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = stateHolder.onOpenAddTopicDialog,
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
                Text(strings.addTopicButton, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        KnowledgeTopicsGrid(
            topics = stateHolder.filteredTopics,
            chaptersByTopic = stateHolder.chaptersByTopic,
            questionCountByTopic = stateHolder.questionCountByTopic,
            onTopicClick = stateHolder.onTopicClick,
            onToggleTopic = stateHolder.onToggleTopic
        )
    }
}
