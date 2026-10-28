package com.example.remind_me.feature.settings.logic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.example.remind_me.core.localization.AppLanguage
import com.example.remind_me.core.model.ReminderSettings
import com.example.remind_me.core.model.StudyMode

@Stable
class SettingsStateHolder(
    val settings: ReminderSettings,
    private val onUpdateSettings: (ReminderSettings) -> Unit,
    private val onChangeLanguage: (String) -> Unit,
    private val onResetSampleData: () -> Unit
) {
    val studyModeOptions: List<StudyMode> = listOf(StudyMode.QUIZ, StudyMode.MEMORY_CARD)
    val languageOptions: List<AppLanguage> = AppLanguage.entries
    val unlockOptions: List<Int> = listOf(1, 2, 3, 5)
    val cooldownOptions: List<Int> = listOf(0, 5, 15, 30)

    val currentLanguage: AppLanguage
        get() = AppLanguage.fromCode(settings.languageCode)

    fun selectStudyMode(mode: StudyMode) {
        onUpdateSettings(settings.copy(studyMode = mode))
    }

    fun selectLanguage(language: AppLanguage) {
        if (settings.languageCode != language.code) {
            onChangeLanguage(language.code)
        }
    }

    fun selectUnlockFrequency(everyNUnlocks: Int) {
        onUpdateSettings(settings.copy(triggerEveryNUnlocks = everyNUnlocks))
    }

    fun selectCooldownMinutes(minutes: Int) {
        onUpdateSettings(settings.copy(cooldownMinutes = minutes))
    }

    fun toggleRequireCorrectAnswer(required: Boolean) {
        onUpdateSettings(settings.copy(requireCorrectAnswerToDismiss = required))
    }

    fun resetSampleData() {
        onResetSampleData()
    }
}

@Composable
fun rememberSettingsStateHolder(
    settings: ReminderSettings,
    onUpdateSettings: (ReminderSettings) -> Unit,
    onChangeLanguage: (String) -> Unit,
    onResetSampleData: () -> Unit
): SettingsStateHolder {
    return remember(settings, onUpdateSettings, onChangeLanguage, onResetSampleData) {
        SettingsStateHolder(
            settings = settings,
            onUpdateSettings = onUpdateSettings,
            onChangeLanguage = onChangeLanguage,
            onResetSampleData = onResetSampleData
        )
    }
}

