package com.example.remind_me.core.localization

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

enum class AppLanguage(val code: String, val displayName: String) {
    VI("vi", "Tiếng Việt"),
    EN("en", "English"),
    JA("ja", "日本語"),
    ZH_CN("zh-cn", "简体中文");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: VI
        }
    }
}

@Immutable
data class AppStrings(
    val languageCode: String,
    val appTitle: String,
    val streakLabel: String,
    val tabOverview: String,
    val tabTopics: String,
    val tabPractice: String,
    val tabSettings: String,
    val statsAnswered: String,
    val statsAccuracy: String,
    val statsTotalQuestions: String,
    val statsActiveTopics: String,
    val lockScreenStatusTitle: String,
    val lockScreenReminderCardTitle: String,
    val serviceStatusEnabledPrefix: String,
    val serviceStatusEnabledSuffix: String,
    val serviceStatusDisabledDesc: String,
    val overlayPermissionWarning: String,
    val grantPermissionButton: String,
    val previewLockScreenNow: String,
    val activeTopicsSectionTitle: String,
    val activeTopicsSectionSubtitle: String,
    val seeAll: String,
    val noChapterSelected: String,
    val topicCardChapterUnit: String,
    val topicCardQuestionUnit: String,
    val searchPlaceholder: String,
    val addTopicButton: String,
    val addTopicDialogTitle: String,
    val addTopicNameLabel: String,
    val addTopicDescLabel: String,
    val saveTopicButton: String,
    val defaultNewTopicChapterTitle: String,
    val defaultNewTopicChapterDesc: String,
    val emptyPracticeTitle: String,
    val emptyPracticeSubtitle: String,
    val practiceQuestionPrefix: String,
    val explanationPrefix: String,
    val nextQuestionButton: String,
    val unlockFrequencyTitle: String,
    val unlockFrequencySubtitle: String,
    val everyNUnlocksPrefix: String,
    val everyNUnlocksSuffix: String,
    val cooldownTitle: String,
    val cooldownSubtitle: String,
    val cooldownAlwaysOn: String,
    val cooldownMinutesSuffix: String,
    val requireCorrectTitle: String,
    val requireCorrectSubtitle: String,
    val resetSampleDataButton: String,
    val backButton: String,
    val enabledOnLockScreen: String,
    val disabledOnLockScreen: String,
    val activeChapterBannerTitle: String,
    val activeChapterBannerDesc: String,
    val previewLockScreenChapter: String,
    val chapterListTitle: String,
    val chapterListSubtitle: String,
    val addChapterButton: String,
    val selectedForLockScreenBadge: String,
    val chapterQuestionsCountSuffix: String,
    val viewQuestionsContentDesc: String,
    val chapterQuestionListHeaderPrefix: String,
    val testThisChapterButton: String,
    val emptyChapterQuestionsText: String,
    val questionItemPrefix: String,
    val correctAnswerPrefix: String,
    val deleteQuestionContentDesc: String,
    val addQuestionToChapterButton: String,
    val studyModeTitle: String,
    val studyModeSubtitle: String,
    val studyModeQuiz: String,
    val studyModeMemoryCard: String,
    val studyModePlaceholderHint: String,
    val languageTitle: String,
    val languageSubtitle: String,
    val addChapterDialogTitle: String,
    val chapterNumberPrefix: String,
    val chapterNumberSuffix: String,
    val addChapterNameLabel: String,
    val addChapterDescLabel: String,
    val saveChapterButton: String,
    val cancelButton: String,
    val addQuestionDialogTitle: String,
    val questionContentLabel: String,
    val optionALabel: String,
    val optionBLabel: String,
    val optionCLabel: String,
    val optionDLabel: String,
    val selectCorrectOptionLabel: String,
    val explanationOptionalLabel: String,
    val memoryCardSectionLabel: String,
    val cardFrontLabel: String,
    val cardBackLabel: String,
    val cardHintLabel: String,
    val saveQuestionButton: String,
    val lockScreenStreakPrefix: String,
    val skipButton: String,
    val defaultTopicFallbackTitle: String,
    val lockScreenQuizHeaderBadge: String,
    val lockScreenCorrectFeedback: String,
    val lockScreenWrongFeedback: String,
    val anotherQuestionButton: String,
    val unlockNowButton: String,
    val lockScreenInstructionRequireCorrect: String,
    val lockScreenInstructionOptional: String,
    val notificationServiceTitle: String,
    val notificationServiceDesc: String,
    val notificationActionReviewNow: String,
    val notificationQuizAlertTitle: String,
    val notificationDefaultQuestionPreview: String,
    val notificationChannelServiceName: String,
    val notificationChannelServiceDesc: String,
    val notificationChannelQuizName: String,
    val notificationChannelQuizDesc: String
) {
    fun formatDefaultChapterTitle(chapterNumber: Int): String =
        "$chapterNumberPrefix$chapterNumber$chapterNumberSuffix"

    fun formatServiceStatusEnabled(everyN: Int): String =
        "$serviceStatusEnabledPrefix$everyN$serviceStatusEnabledSuffix"

    fun formatTopicChapterQuestionCount(chapterCount: Int, questionCount: Int): String =
        "$chapterCount $topicCardChapterUnit • $questionCount $topicCardQuestionUnit"

    fun formatPracticeQuestionCounter(current: Int, total: Int): String =
        "$practiceQuestionPrefix $current/$total"

    fun formatExplanation(explanation: String): String =
        "$explanationPrefix: $explanation"

    fun formatEveryNUnlocks(count: Int): String =
        "$everyNUnlocksPrefix$count$everyNUnlocksSuffix"

    fun formatCooldownOption(minutes: Int): String =
        if (minutes == 0) cooldownAlwaysOn else "$minutes$cooldownMinutesSuffix"

    fun formatChapterQuestionsCount(count: Int): String =
        "$count $chapterQuestionsCountSuffix"

    fun formatChapterQuestionListHeader(count: Int): String =
        "$chapterQuestionListHeaderPrefix ($count)"

    fun formatQuestionNumberPrefix(number: Int, questionText: String): String =
        "$questionItemPrefix $number: $questionText"

    fun formatCorrectAnswerLabel(optionLabel: String, optionText: String): String =
        "$correctAnswerPrefix: $optionLabel. $optionText"

    fun formatLockScreenStreak(streak: Int): String =
        "$lockScreenStreakPrefix: $streak"
}

fun getAppStrings(languageCode: String): AppStrings {
    return when (AppLanguage.fromCode(languageCode)) {
        AppLanguage.VI -> VietnameseAppStrings
        AppLanguage.EN -> EnglishAppStrings
        AppLanguage.JA -> JapaneseAppStrings
        AppLanguage.ZH_CN -> ChineseSimplifiedAppStrings
    }
}

val LocalAppStrings = staticCompositionLocalOf { VietnameseAppStrings }
