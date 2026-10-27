package com.example.remind_me.core.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.remind_me.core.localization.getAppStrings
import com.example.remind_me.core.model.ChapterItem
import com.example.remind_me.core.model.LockScreenQuizPayload
import com.example.remind_me.core.model.QuizQuestion
import com.example.remind_me.core.model.ReminderSettings
import com.example.remind_me.core.model.StudyMode
import com.example.remind_me.core.model.TopicItem
import com.example.remind_me.core.network.KnowledgeDataSource
import com.example.remind_me.core.network.MockAssetKnowledgeDataSource
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class KnowledgeRepository(
    context: Context,
    private val dataSource: KnowledgeDataSource = MockAssetKnowledgeDataSource(context.applicationContext)
) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    init {
        if (prefs.getInt(KEY_DATA_VERSION, 0) < CURRENT_DATA_VERSION) {
            seedDefaultData(getSettings().languageCode)
        }
    }

    fun getTopics(): List<TopicItem> {
        val raw = prefs.getString(KEY_TOPICS, null) ?: return emptyList()
        val array = JSONArray(raw)
        val result = mutableListOf<TopicItem>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val modesArr = obj.optJSONArray("supportedModes")
            val modes = mutableListOf<StudyMode>()
            if (modesArr != null) {
                for (j in 0 until modesArr.length()) {
                    modes.add(StudyMode.fromName(modesArr.getString(j)))
                }
            }
            result.add(
                TopicItem(
                    id = obj.getString("id"),
                    title = obj.getString("title"),
                    description = obj.optString("description", ""),
                    iconName = obj.optString("iconName", "Code"),
                    isEnabled = obj.optBoolean("isEnabled", true),
                    selectedChapterId = obj.optString("selectedChapterId", ""),
                    supportedModes = modes.ifEmpty { listOf(StudyMode.QUIZ, StudyMode.MEMORY_CARD) },
                    preferredStudyMode = StudyMode.fromName(obj.optString("preferredStudyMode", "QUIZ"))
                )
            )
        }
        return result
    }

    fun saveTopics(topics: List<TopicItem>) {
        val array = JSONArray()
        topics.forEach { topic ->
            val modesArr = JSONArray()
            topic.supportedModes.forEach { modesArr.put(it.name) }
            val obj = JSONObject().apply {
                put("id", topic.id)
                put("title", topic.title)
                put("description", topic.description)
                put("iconName", topic.iconName)
                put("isEnabled", topic.isEnabled)
                put("selectedChapterId", topic.selectedChapterId)
                put("supportedModes", modesArr)
                put("preferredStudyMode", topic.preferredStudyMode.name)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_TOPICS, array.toString()).apply()
    }

    fun toggleTopicEnabled(topicId: String) {
        val updated = getTopics().map {
            if (it.id == topicId) it.copy(isEnabled = !it.isEnabled) else it
        }
        saveTopics(updated)
    }

    fun updateTopicPreferredStudyMode(topicId: String, mode: StudyMode) {
        val updated = getTopics().map {
            if (it.id == topicId) it.copy(preferredStudyMode = mode) else it
        }
        saveTopics(updated)
    }

    fun selectChapterForTopic(topicId: String, chapterId: String) {
        val updated = getTopics().map {
            if (it.id == topicId) {
                it.copy(selectedChapterId = chapterId, isEnabled = true)
            } else {
                it
            }
        }
        saveTopics(updated)
    }

    fun addTopic(title: String, description: String, iconName: String = "Code"): TopicItem {
        val topics = getTopics().toMutableList()
        val topicId = "topic_${UUID.randomUUID().toString().take(8)}"
        val strings = getAppStrings(getSettings().languageCode)
        val defaultChapter = addChapter(
            topicId = topicId,
            title = strings.defaultNewTopicChapterTitle,
            description = strings.defaultNewTopicChapterDesc
        )
        val newTopic = TopicItem(
            id = topicId,
            title = title.trim(),
            description = description.trim(),
            iconName = iconName,
            isEnabled = true,
            selectedChapterId = defaultChapter.id
        )
        topics.add(newTopic)
        saveTopics(topics)
        return newTopic
    }

    fun getChapters(): List<ChapterItem> {
        val raw = prefs.getString(KEY_CHAPTERS, null) ?: return emptyList()
        val array = JSONArray(raw)
        val result = mutableListOf<ChapterItem>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            result.add(
                ChapterItem(
                    id = obj.getString("id"),
                    topicId = obj.getString("topicId"),
                    chapterNumber = obj.optInt("chapterNumber", i + 1),
                    title = obj.getString("title"),
                    description = obj.optString("description", "")
                )
            )
        }
        return result
    }

    fun getChaptersByTopic(topicId: String): List<ChapterItem> {
        return getChapters()
            .filter { it.topicId == topicId }
            .sortedBy { it.chapterNumber }
    }

    fun saveChapters(chapters: List<ChapterItem>) {
        val array = JSONArray()
        chapters.forEach { chapter ->
            val obj = JSONObject().apply {
                put("id", chapter.id)
                put("topicId", chapter.topicId)
                put("chapterNumber", chapter.chapterNumber)
                put("title", chapter.title)
                put("description", chapter.description)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_CHAPTERS, array.toString()).apply()
    }

    fun addChapter(topicId: String, title: String, description: String): ChapterItem {
        val allChapters = getChapters().toMutableList()
        val nextNumber = (allChapters.filter { it.topicId == topicId }.maxOfOrNull { it.chapterNumber } ?: 0) + 1
        val newChapter = ChapterItem(
            id = "chap_${UUID.randomUUID().toString().take(8)}",
            topicId = topicId,
            chapterNumber = nextNumber,
            title = title.trim(),
            description = description.trim()
        )
        allChapters.add(newChapter)
        saveChapters(allChapters)

        val topics = getTopics()
        val targetTopic = topics.find { it.id == topicId }
        if (targetTopic != null && targetTopic.selectedChapterId.isBlank()) {
            selectChapterForTopic(topicId, newChapter.id)
        }
        return newChapter
    }

    fun getQuestions(): List<QuizQuestion> {
        val raw = prefs.getString(KEY_QUESTIONS, null) ?: return emptyList()
        val array = JSONArray(raw)
        val result = mutableListOf<QuizQuestion>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val optionsArray = obj.getJSONArray("options")
            val options = mutableListOf<String>()
            for (j in 0 until optionsArray.length()) {
                options.add(optionsArray.getString(j))
            }
            val questionText = obj.getString("question")
            val correctIndex = obj.getInt("correctOptionIndex")
            result.add(
                QuizQuestion(
                    id = obj.getString("id"),
                    topicId = obj.getString("topicId"),
                    chapterId = obj.optString("chapterId", ""),
                    question = questionText,
                    codeSnippet = obj.optString("codeSnippet", ""),
                    options = options,
                    correctOptionIndex = correctIndex,
                    explanation = obj.optString("explanation", ""),
                    correctCount = obj.optInt("correctCount", 0),
                    wrongCount = obj.optInt("wrongCount", 0),
                    cardFront = obj.optString("cardFront", questionText),
                    cardBack = obj.optString("cardBack", options.getOrElse(correctIndex) { "" }),
                    cardHint = obj.optString("cardHint", ""),
                    memoryBoxLevel = obj.optInt("memoryBoxLevel", 1),
                    nextReviewAtMillis = obj.optLong("nextReviewAtMillis", 0L)
                )
            )
        }
        return result
    }

    fun saveQuestions(questions: List<QuizQuestion>) {
        val array = JSONArray()
        questions.forEach { q ->
            val optionsArray = JSONArray()
            q.options.forEach { optionsArray.put(it) }
            val obj = JSONObject().apply {
                put("id", q.id)
                put("topicId", q.topicId)
                put("chapterId", q.chapterId)
                put("question", q.question)
                put("codeSnippet", q.codeSnippet)
                put("options", optionsArray)
                put("correctOptionIndex", q.correctOptionIndex)
                put("explanation", q.explanation)
                put("correctCount", q.correctCount)
                put("wrongCount", q.wrongCount)
                put("cardFront", q.cardFront)
                put("cardBack", q.cardBack)
                put("cardHint", q.cardHint)
                put("memoryBoxLevel", q.memoryBoxLevel)
                put("nextReviewAtMillis", q.nextReviewAtMillis)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_QUESTIONS, array.toString()).apply()
    }

    fun addQuestion(
        topicId: String,
        chapterId: String,
        question: String,
        options: List<String>,
        correctOptionIndex: Int,
        explanation: String,
        cardFront: String = "",
        cardBack: String = "",
        cardHint: String = ""
    ): QuizQuestion {
        val all = getQuestions().toMutableList()
        val trimmedQuestion = question.trim()
        val trimmedOptions = options.map { it.trim() }
        val safeIdx = correctOptionIndex.coerceIn(0, trimmedOptions.lastIndex.coerceAtLeast(0))
        val newQuestion = QuizQuestion(
            id = "q_${UUID.randomUUID().toString().take(8)}",
            topicId = topicId,
            chapterId = chapterId,
            question = trimmedQuestion,
            options = trimmedOptions,
            correctOptionIndex = safeIdx,
            explanation = explanation.trim(),
            cardFront = cardFront.trim().ifBlank { trimmedQuestion },
            cardBack = cardBack.trim().ifBlank { trimmedOptions.getOrElse(safeIdx) { "" } },
            cardHint = cardHint.trim()
        )
        all.add(0, newQuestion)
        saveQuestions(all)
        return newQuestion
    }

    fun deleteQuestion(questionId: String) {
        val filtered = getQuestions().filterNot { it.id == questionId }
        saveQuestions(filtered)
    }

    fun getNextLockScreenQuestion(
        filterTopicId: String? = null,
        filterChapterId: String? = null
    ): LockScreenQuizPayload? {
        val topics = getTopics()
        val chapters = getChapters()
        val allQuestions = getQuestions()
        if (allQuestions.isEmpty()) return null

        val pool = when {
            !filterChapterId.isNullOrBlank() -> {
                allQuestions.filter { it.chapterId == filterChapterId }
            }
            !filterTopicId.isNullOrBlank() -> {
                val topic = topics.find { it.id == filterTopicId }
                val activeChapId = topic?.selectedChapterId.orEmpty()
                allQuestions.filter {
                    it.topicId == filterTopicId &&
                        (activeChapId.isBlank() || it.chapterId == activeChapId)
                }
            }
            else -> {
                val enabledTopics = topics.filter { it.isEnabled }
                val allowedChapterIds = enabledTopics
                    .mapNotNull { t -> t.selectedChapterId.takeIf { it.isNotBlank() } }
                    .toSet()

                val chapterFiltered = allQuestions.filter { it.chapterId in allowedChapterIds }
                if (chapterFiltered.isNotEmpty()) {
                    chapterFiltered
                } else {
                    val enabledTopicIds = enabledTopics.map { it.id }.toSet()
                    allQuestions.filter { it.topicId in enabledTopicIds }
                }
            }
        }.ifEmpty { allQuestions }

        val weighted = pool.shuffled().sortedBy { it.correctCount - it.wrongCount }
        val chosen = weighted.take(3).random()
        val topic = topics.find { it.id == chosen.topicId }
        val chapter = chapters.find { it.id == chosen.chapterId }
        val currentSettings = getSettings()
        return LockScreenQuizPayload(
            question = chosen,
            topic = topic,
            chapter = chapter,
            activeStudyMode = topic?.preferredStudyMode ?: currentSettings.studyMode
        )
    }

    fun recordAnswer(questionId: String, isCorrect: Boolean) {
        val updatedQuestions = getQuestions().map { q ->
            if (q.id == questionId) {
                val nextLevel = if (isCorrect) (q.memoryBoxLevel + 1).coerceAtMost(5) else 1
                if (isCorrect) {
                    q.copy(
                        correctCount = q.correctCount + 1,
                        memoryBoxLevel = nextLevel
                    )
                } else {
                    q.copy(
                        wrongCount = q.wrongCount + 1,
                        memoryBoxLevel = nextLevel
                    )
                }
            } else {
                q
            }
        }
        saveQuestions(updatedQuestions)

        val current = getSettings()
        val updatedSettings = current.copy(
            totalAnswered = current.totalAnswered + 1,
            totalCorrect = if (isCorrect) current.totalCorrect + 1 else current.totalCorrect,
            currentStreak = if (isCorrect) current.currentStreak + 1 else 0
        )
        saveSettings(updatedSettings)
    }

    fun getSettings(): ReminderSettings {
        return ReminderSettings(
            isServiceEnabled = prefs.getBoolean(KEY_SERVICE_ENABLED, false),
            triggerEveryNUnlocks = prefs.getInt(KEY_TRIGGER_EVERY_N, 1),
            cooldownMinutes = prefs.getInt(KEY_COOLDOWN_MINUTES, 0),
            requireCorrectAnswerToDismiss = prefs.getBoolean(KEY_REQUIRE_CORRECT, true),
            totalAnswered = prefs.getInt(KEY_TOTAL_ANSWERED, 0),
            totalCorrect = prefs.getInt(KEY_TOTAL_CORRECT, 0),
            currentStreak = prefs.getInt(KEY_CURRENT_STREAK, 0),
            languageCode = prefs.getString(KEY_LANGUAGE_CODE, "vi") ?: "vi",
            studyMode = StudyMode.fromName(prefs.getString(KEY_STUDY_MODE, "QUIZ"))
        )
    }

    fun saveSettings(settings: ReminderSettings) {
        val previousLang = prefs.getString(KEY_LANGUAGE_CODE, "vi") ?: "vi"
        prefs.edit()
            .putBoolean(KEY_SERVICE_ENABLED, settings.isServiceEnabled)
            .putInt(KEY_TRIGGER_EVERY_N, settings.triggerEveryNUnlocks)
            .putInt(KEY_COOLDOWN_MINUTES, settings.cooldownMinutes)
            .putBoolean(KEY_REQUIRE_CORRECT, settings.requireCorrectAnswerToDismiss)
            .putInt(KEY_TOTAL_ANSWERED, settings.totalAnswered)
            .putInt(KEY_TOTAL_CORRECT, settings.totalCorrect)
            .putInt(KEY_CURRENT_STREAK, settings.currentStreak)
            .putString(KEY_LANGUAGE_CODE, settings.languageCode)
            .putString(KEY_STUDY_MODE, settings.studyMode.name)
            .apply()

        if (!previousLang.equals(settings.languageCode, ignoreCase = true)) {
            syncContentForLanguagePreservingProgress(settings.languageCode)
        }
    }

    /**
     * Reloads localized topic/chapter/question content from assets/mock_api/{lang}/topics/
     * while preserving user progress (isEnabled, selectedChapterId, correctCount, wrongCount, custom items).
     */
    fun syncContentForLanguagePreservingProgress(languageCode: String) {
        val currentSettings = getSettings()
        if (!currentSettings.languageCode.equals(languageCode, ignoreCase = true)) {
            prefs.edit().putString(KEY_LANGUAGE_CODE, languageCode).apply()
        }
        val existingTopicsById = getTopics().associateBy { it.id }
        val existingQuestionsById = getQuestions().associateBy { it.id }

        val bundles = dataSource.fetchTopicBundles(languageCode)
        val remoteTopicIds = bundles.map { it.topic.id }.toSet()
        val remoteQuestionIds = bundles.flatMap { it.questions }.map { it.id }.toSet()

        val mergedTopics = bundles.map { bundle ->
            val old = existingTopicsById[bundle.topic.id]
            bundle.topic.toDomainModel(
                isEnabledOverride = old?.isEnabled,
                selectedChapterIdOverride = old?.selectedChapterId,
                preferredModeOverride = old?.preferredStudyMode
            )
        } + existingTopicsById.values.filter { it.id !in remoteTopicIds }

        val mergedChapters = bundles.flatMap { bundle ->
            bundle.chapters.map { it.toDomainModel() }
        } + getChapters().filter { it.topicId !in remoteTopicIds }

        val mergedQuestions = bundles.flatMap { bundle ->
            bundle.questions.map { qDto ->
                val oldQ = existingQuestionsById[qDto.id]
                qDto.toDomainModel(
                    correctCount = oldQ?.correctCount ?: 0,
                    wrongCount = oldQ?.wrongCount ?: 0,
                    savedMemoryBoxLevel = oldQ?.memoryBoxLevel,
                    savedNextReviewAtMillis = oldQ?.nextReviewAtMillis
                )
            }
        } + existingQuestionsById.values.filter { it.id !in remoteQuestionIds }

        saveTopics(mergedTopics)
        saveChapters(mergedChapters)
        saveQuestions(mergedQuestions)
    }

    fun shouldTriggerOnScreenOn(): Boolean {
        val settings = getSettings()
        if (!settings.isServiceEnabled) return false

        val count = prefs.getInt(KEY_SCREEN_ON_COUNTER, 0) + 1
        if (count < settings.triggerEveryNUnlocks) {
            prefs.edit().putInt(KEY_SCREEN_ON_COUNTER, count).apply()
            return false
        }

        val now = System.currentTimeMillis()
        val lastTriggered = prefs.getLong(KEY_LAST_TRIGGER_TIME, 0L)
        val cooldownMillis = settings.cooldownMinutes * 60_000L
        if (cooldownMillis > 0 && (now - lastTriggered) < cooldownMillis) {
            return false
        }

        prefs.edit()
            .putInt(KEY_SCREEN_ON_COUNTER, 0)
            .putLong(KEY_LAST_TRIGGER_TIME, now)
            .apply()
        return true
    }

    fun resetToDefaultData(languageCode: String = getSettings().languageCode) {
        seedDefaultData(languageCode)
    }

    private fun seedDefaultData(languageCode: String) {
        val bundles = dataSource.fetchTopicBundles(languageCode)
        saveTopics(bundles.map { it.topic.toDomainModel() })
        saveChapters(bundles.flatMap { b -> b.chapters.map { it.toDomainModel() } })
        saveQuestions(bundles.flatMap { b -> b.questions.map { it.toDomainModel() } })
        prefs.edit().putInt(KEY_DATA_VERSION, CURRENT_DATA_VERSION).apply()
    }

    companion object {
        private const val PREFS_NAME = "remind_me_knowledge_prefs"
        private const val KEY_DATA_VERSION = "key_data_version"
        private const val CURRENT_DATA_VERSION = 5
        private const val KEY_TOPICS = "key_topics"
        private const val KEY_CHAPTERS = "key_chapters"
        private const val KEY_QUESTIONS = "key_questions"
        private const val KEY_SERVICE_ENABLED = "key_service_enabled"
        private const val KEY_TRIGGER_EVERY_N = "key_trigger_every_n"
        private const val KEY_COOLDOWN_MINUTES = "key_cooldown_minutes"
        private const val KEY_REQUIRE_CORRECT = "key_require_correct"
        private const val KEY_TOTAL_ANSWERED = "key_total_answered"
        private const val KEY_TOTAL_CORRECT = "key_total_correct"
        private const val KEY_CURRENT_STREAK = "key_current_streak"
        private const val KEY_LANGUAGE_CODE = "key_language_code"
        private const val KEY_STUDY_MODE = "key_study_mode"
        private const val KEY_SCREEN_ON_COUNTER = "key_screen_on_counter"
        private const val KEY_LAST_TRIGGER_TIME = "key_last_trigger_time"
    }
}
