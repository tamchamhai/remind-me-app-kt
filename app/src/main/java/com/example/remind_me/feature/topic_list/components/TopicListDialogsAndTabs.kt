package com.example.remind_me.feature.topic_list.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remind_me.core.model.ReminderSettings
import com.example.remind_me.core.theme.RemindMeTheme

@Composable
fun LearningStatsSummaryCard(
    settings: ReminderSettings,
    totalQuestions: Int,
    activeTopicsCount: Int,
    modifier: Modifier = Modifier
) {
    val colors = RemindMeTheme.colors
    val strings = RemindMeTheme.strings
    val accuracy = if (settings.totalAnswered > 0) {
        (settings.totalCorrect * 100) / settings.totalAnswered
    } else {
        0
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = colors.bannerBackground,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatColumnItem(value = "${settings.totalAnswered}", label = strings.statsAnswered)
            VerticalDivider(
                modifier = Modifier.height(36.dp),
                color = Color.White.copy(alpha = 0.25f)
            )
            StatColumnItem(value = "$accuracy%", label = strings.statsAccuracy)
            VerticalDivider(
                modifier = Modifier.height(36.dp),
                color = Color.White.copy(alpha = 0.25f)
            )
            StatColumnItem(value = "$totalQuestions", label = strings.statsTotalQuestions)
            VerticalDivider(
                modifier = Modifier.height(36.dp),
                color = Color.White.copy(alpha = 0.25f)
            )
            StatColumnItem(value = "$activeTopicsCount", label = strings.statsActiveTopics)
        }
    }
}

@Composable
private fun StatColumnItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.85f)
        )
    }
}

@Composable
fun AddTopicDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String) -> Unit
) {
    val colors = RemindMeTheme.colors
    val strings = RemindMeTheme.strings
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        titleContentColor = colors.textPrimary,
        textContentColor = colors.textPrimary,
        title = { Text(strings.addTopicDialogTitle, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(strings.addTopicNameLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(strings.addTopicDescLabel) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, description)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
            ) {
                Text(strings.saveTopicButton)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancelButton, color = colors.textSecondary)
            }
        }
    )
}
