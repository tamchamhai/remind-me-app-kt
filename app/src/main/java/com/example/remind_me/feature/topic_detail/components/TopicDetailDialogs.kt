package com.example.remind_me.feature.topic_detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.remind_me.core.model.ChapterItem
import com.example.remind_me.core.theme.RemindMeTheme

@Composable
fun AddChapterDialog(
    nextChapterNumber: Int,
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String) -> Unit
) {
    val colors = RemindMeTheme.colors
    val strings = RemindMeTheme.strings
    var title by remember(nextChapterNumber, strings.languageCode) {
        mutableStateOf(strings.formatDefaultChapterTitle(nextChapterNumber))
    }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        titleContentColor = colors.textPrimary,
        textContentColor = colors.textPrimary,
        title = { Text(strings.addChapterDialogTitle, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(strings.addChapterNameLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(strings.addChapterDescLabel) },
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
                Text(strings.saveChapterButton)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancelButton, color = colors.textSecondary)
            }
        }
    )
}

@Composable
fun AddChapterQuestionDialog(
    chapter: ChapterItem,
    onDismiss: () -> Unit,
    onConfirm: (
        question: String,
        options: List<String>,
        correctIndex: Int,
        explanation: String,
        cardFront: String,
        cardBack: String,
        cardHint: String
    ) -> Unit
) {
    val colors = RemindMeTheme.colors
    val strings = RemindMeTheme.strings
    var questionText by remember { mutableStateOf("") }
    var optionA by remember { mutableStateOf("") }
    var optionB by remember { mutableStateOf("") }
    var optionC by remember { mutableStateOf("") }
    var optionD by remember { mutableStateOf("") }
    var correctIndex by remember { mutableIntStateOf(0) }
    var explanation by remember { mutableStateOf("") }

    // Memory Card placeholder fields
    var cardFront by remember { mutableStateOf("") }
    var cardBack by remember { mutableStateOf("") }
    var cardHint by remember { mutableStateOf("") }

    val isFormValid = questionText.isNotBlank() &&
        optionA.isNotBlank() &&
        optionB.isNotBlank() &&
        optionC.isNotBlank() &&
        optionD.isNotBlank()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = colors.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = strings.addQuestionDialogTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = colors.textPrimary
                )
                Text(
                    text = chapter.title,
                    fontSize = 13.sp,
                    color = colors.primary,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text(strings.questionContentLabel) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = optionA,
                    onValueChange = { optionA = it },
                    label = { Text(strings.optionALabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = optionB,
                    onValueChange = { optionB = it },
                    label = { Text(strings.optionBLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = optionC,
                    onValueChange = { optionC = it },
                    label = { Text(strings.optionCLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = optionD,
                    onValueChange = { optionD = it },
                    label = { Text(strings.optionDLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = strings.selectCorrectOptionLabel,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textSecondary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("A", "B", "C", "D").forEachIndexed { index, label ->
                        FilterChip(
                            selected = correctIndex == index,
                            onClick = { correctIndex = index },
                            label = { Text(label) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                OutlinedTextField(
                    value = explanation,
                    onValueChange = { explanation = it },
                    label = { Text(strings.explanationOptionalLabel) },
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(color = colors.border)

                Text(
                    text = strings.memoryCardSectionLabel,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.primary
                )

                OutlinedTextField(
                    value = cardFront,
                    onValueChange = { cardFront = it },
                    label = { Text(strings.cardFrontLabel) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = cardBack,
                    onValueChange = { cardBack = it },
                    label = { Text(strings.cardBackLabel) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = cardHint,
                    onValueChange = { cardHint = it },
                    label = { Text(strings.cardHintLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(strings.cancelButton, color = colors.textSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onConfirm(
                                questionText,
                                listOf(optionA, optionB, optionC, optionD),
                                correctIndex,
                                explanation,
                                cardFront,
                                cardBack,
                                cardHint
                            )
                        },
                        enabled = isFormValid,
                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                    ) {
                        Text(strings.saveQuestionButton)
                    }
                }
            }
        }
    }
}
