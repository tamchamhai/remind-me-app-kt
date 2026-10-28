package com.example.remind_me.feature.settings.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remind_me.core.model.ReminderSettings
import com.example.remind_me.core.model.StudyMode
import com.example.remind_me.core.theme.RemindMeTheme
import com.example.remind_me.feature.settings.components.SettingsDropdownSelector
import com.example.remind_me.feature.settings.logic.SettingsStateHolder
import com.example.remind_me.feature.settings.logic.rememberSettingsStateHolder

@Composable
fun SettingsScreen(
    settings: ReminderSettings,
    onUpdateSettings: (ReminderSettings) -> Unit,
    onChangeLanguage: (String) -> Unit,
    onResetSampleData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stateHolder = rememberSettingsStateHolder(
        settings = settings,
        onUpdateSettings = onUpdateSettings,
        onChangeLanguage = onChangeLanguage,
        onResetSampleData = onResetSampleData
    )
    SettingsContent(
        stateHolder = stateHolder,
        modifier = modifier
    )
}

@Composable
fun SettingsContent(
    stateHolder: SettingsStateHolder,
    modifier: Modifier = Modifier
) {
    val colors = RemindMeTheme.colors
    val strings = RemindMeTheme.strings
    val settings = stateHolder.settings

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Study Mode Placeholder Toggle (Quiz vs Memory Card)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.border),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = strings.studyModeTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = strings.studyModeSubtitle,
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsDropdownSelector(
                    options = stateHolder.studyModeOptions,
                    selectedOption = settings.studyMode,
                    optionLabel = { mode ->
                        when (mode) {
                            StudyMode.QUIZ -> strings.studyModeQuiz
                            StudyMode.MEMORY_CARD -> strings.studyModeMemoryCard
                        }
                    },
                    onOptionSelected = stateHolder::selectStudyMode
                )
                if (settings.studyMode == StudyMode.MEMORY_CARD) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = colors.warningContainer
                    ) {
                        Text(
                            text = strings.studyModePlaceholderHint,
                            fontSize = 12.sp,
                            color = colors.warningText,
                            modifier = Modifier.padding(10.dp),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // 2. Language Selector (UI Local + Server Mock API Data Language)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.border),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = strings.languageTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = strings.languageSubtitle,
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsDropdownSelector(
                    options = stateHolder.languageOptions,
                    selectedOption = stateHolder.currentLanguage,
                    optionLabel = { it.displayName },
                    onOptionSelected = stateHolder::selectLanguage
                )
            }
        }

        // 3. Unlock Frequency
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.border),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = strings.unlockFrequencyTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = strings.unlockFrequencySubtitle,
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsDropdownSelector(
                    options = stateHolder.unlockOptions,
                    selectedOption = settings.triggerEveryNUnlocks,
                    optionLabel = { count -> strings.formatEveryNUnlocks(count) },
                    onOptionSelected = stateHolder::selectUnlockFrequency
                )
            }
        }

        // 4. Cooldown
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.border),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = strings.cooldownTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = strings.cooldownSubtitle,
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsDropdownSelector(
                    options = stateHolder.cooldownOptions,
                    selectedOption = settings.cooldownMinutes,
                    optionLabel = { mins -> strings.formatCooldownOption(mins) },
                    onOptionSelected = stateHolder::selectCooldownMinutes
                )
            }
        }

        // 5. Require Correct Answer
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.border),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.requireCorrectTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = strings.requireCorrectSubtitle,
                        fontSize = 13.sp,
                        color = colors.textSecondary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Switch(
                    checked = settings.requireCorrectAnswerToDismiss,
                    onCheckedChange = stateHolder::toggleRequireCorrectAnswer,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = colors.primary
                    )
                )
            }
        }

        OutlinedButton(
            onClick = stateHolder::resetSampleData,
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, colors.border),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = colors.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(strings.resetSampleDataButton, color = colors.primary)
        }
    }
}

