package com.example.remind_me.feature.topic_detail.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remind_me.core.model.ChapterItem
import com.example.remind_me.core.model.QuizQuestion
import com.example.remind_me.core.theme.RemindMeTheme

@Composable
fun ChapterSelectorCard(
    chapter: ChapterItem,
    questions: List<QuizQuestion>,
    isSelectedForLockScreen: Boolean,
    isExpanded: Boolean,
    onSelectForLockScreen: () -> Unit,
    onToggleExpand: () -> Unit,
    onAddQuestionToChapter: () -> Unit,
    onDeleteQuestion: (String) -> Unit,
    onPreviewChapterOnLockScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = RemindMeTheme.colors
    val strings = RemindMeTheme.strings
    val borderColor = if (isSelectedForLockScreen) colors.primary else colors.border
    val containerColor = if (isSelectedForLockScreen) colors.selectedSurface else colors.surface

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onToggleExpand() },
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        border = BorderStroke(if (isSelectedForLockScreen) 1.8.dp else 1.dp, borderColor),
        shadowElevation = if (isSelectedForLockScreen) 2.dp else 0.5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelectedForLockScreen,
                    onClick = onSelectForLockScreen,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = colors.primary,
                        unselectedColor = colors.textSecondary
                    )
                )

                Spacer(modifier = Modifier.width(6.dp))

                Column(modifier = Modifier.weight(1f)) {
                    if (isSelectedForLockScreen) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = colors.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = strings.selectedForLockScreenBadge,
                                color = if (colors.isDark) Color(0xFF00252A) else Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = chapter.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )

                    if (chapter.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = chapter.description,
                            fontSize = 12.sp,
                            color = colors.textSecondary,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = strings.formatChapterQuestionsCount(questions.size),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.primary
                    )
                }

                IconButton(onClick = onToggleExpand) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = strings.viewQuestionsContentDesc,
                        tint = colors.textSecondary
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = colors.border)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.formatChapterQuestionListHeader(questions.size),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )

                        TextButton(onClick = onPreviewChapterOnLockScreen) {
                            Icon(
                                imageVector = Icons.Default.LockClock,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = colors.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = strings.testThisChapterButton,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary
                            )
                        }
                    }

                    if (questions.isEmpty()) {
                        Text(
                            text = strings.emptyChapterQuestionsText,
                            fontSize = 13.sp,
                            color = colors.textSecondary,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    } else {
                        questions.forEachIndexed { idx, q ->
                            val correctLabel = listOf("A", "B", "C", "D")
                                .getOrElse(q.correctOptionIndex) { "A" }
                            val correctText = q.options.getOrElse(q.correctOptionIndex) { "" }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = colors.surfaceMuted,
                                border = BorderStroke(1.dp, colors.border),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = strings.formatQuestionNumberPrefix(idx + 1, q.question),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.textPrimary,
                                            lineHeight = 18.sp
                                        )
                                        if (q.codeSnippet.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color(0xFF090D16),
                                                border = BorderStroke(1.dp, Color(0xFF1E293B)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = q.codeSnippet,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 11.sp,
                                                    lineHeight = 16.sp,
                                                    color = Color(0xFF80CBC4),
                                                    modifier = Modifier.padding(8.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = strings.formatCorrectAnswerLabel(correctLabel, correctText),
                                            fontSize = 12.sp,
                                            color = if (colors.isDark) Color(0xFF81C784) else Color(0xFF2E7D32),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    IconButton(
                                        onClick = { onDeleteQuestion(q.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = strings.deleteQuestionContentDesc,
                                            tint = Color(0xFFEF5350),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = onAddQuestionToChapter,
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, colors.border),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = colors.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.addQuestionToChapterButton,
                            color = colors.primary
                        )
                    }
                }
            }
        }
    }
}
