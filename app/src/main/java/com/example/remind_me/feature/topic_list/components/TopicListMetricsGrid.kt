package com.example.remind_me.feature.topic_list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remind_me.core.model.ChapterItem
import com.example.remind_me.core.model.TopicItem
import com.example.remind_me.core.theme.RemindMeTheme
import com.example.remind_me.feature.topic_list.data.MetricItem

@Composable
fun TopicListMetricsGrid(metrics: List<MetricItem>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        metrics.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { metric ->
                    MetricCard(
                        title = metric.title,
                        icon = metric.icon,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun KnowledgeTopicsGrid(
    topics: List<TopicItem>,
    chaptersByTopic: Map<String, List<ChapterItem>>,
    questionCountByTopic: Map<String, Int>,
    onTopicClick: (TopicItem) -> Unit,
    onToggleTopic: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = RemindMeTheme.strings
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        topics.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { topic ->
                    val topicChapters = chaptersByTopic[topic.id].orEmpty()
                    val selectedChapter = topicChapters.find { it.id == topic.selectedChapterId }
                        ?: topicChapters.firstOrNull()
                    KnowledgeTopicCard(
                        topic = topic,
                        chapterCount = topicChapters.size,
                        selectedChapterTitle = selectedChapter?.title ?: strings.noChapterSelected,
                        questionCount = questionCountByTopic[topic.id] ?: 0,
                        onClick = { onTopicClick(topic) },
                        onToggle = { onToggleTopic(topic.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun KnowledgeTopicCard(
    topic: TopicItem,
    chapterCount: Int,
    selectedChapterTitle: String,
    questionCount: Int,
    onClick: () -> Unit,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RemindMeTheme.colors
    val strings = RemindMeTheme.strings

    Surface(
        modifier = modifier
            .height(172.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = colors.surface,
        border = BorderStroke(1.dp, if (topic.isEnabled) colors.primary.copy(alpha = 0.4f) else colors.border),
        shadowElevation = 1.5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (topic.isEnabled) colors.primaryContainer else colors.surfaceMuted
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = resolveTopicIcon(topic.iconName),
                        contentDescription = topic.title,
                        tint = if (topic.isEnabled) colors.onPrimaryContainer else colors.textSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Switch(
                    checked = topic.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = colors.primary
                    )
                )
            }

            Column {
                Text(
                    text = topic.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = selectedChapterTitle,
                    fontSize = 11.sp,
                    color = if (topic.isEnabled) colors.primary else colors.textSecondary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = strings.formatTopicChapterQuestionCount(chapterCount, questionCount),
                    fontSize = 11.sp,
                    color = colors.textSecondary
                )
            }
        }
    }
}

@Composable
fun MetricCard(title: String, icon: ImageVector, modifier: Modifier = Modifier) {
    val colors = RemindMeTheme.colors

    Surface(
        modifier = modifier
            .height(110.dp)
            .clickable { },
        shape = RoundedCornerShape(16.dp),
        color = colors.surface,
        border = BorderStroke(1.dp, colors.border),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = colors.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textPrimary
            )
        }
    }
}

fun resolveTopicIcon(iconName: String): ImageVector {
    return when (iconName) {
        "Language" -> Icons.Default.Language
        "Code" -> Icons.Default.Code
        "Bolt" -> Icons.Default.Bolt
        "Web" -> Icons.Default.Web
        "Storage" -> Icons.Default.Storage
        "PhoneAndroid" -> Icons.Default.PhoneAndroid
        "Memory" -> Icons.Default.Memory
        "Lightbulb" -> Icons.Default.Lightbulb
        else -> Icons.Default.School
    }
}
