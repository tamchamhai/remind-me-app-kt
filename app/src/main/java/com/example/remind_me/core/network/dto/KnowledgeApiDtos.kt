package com.example.remind_me.core.network.dto

import com.example.remind_me.core.model.ChapterItem
import com.example.remind_me.core.model.QuizQuestion
import com.example.remind_me.core.model.StudyMode
import com.example.remind_me.core.model.TopicItem
import org.json.JSONObject

data class TopicBundleResponseDto(
    val topic: TopicDto,
    val chapters: List<ChapterDto>,
    val questions: List<QuestionDto>
)

data class TopicDto(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val isEnabledByDefault: Boolean,
    val defaultSelectedChapterId: String,
    val supportedModes: List<String>,
    val preferredStudyMode: String
) {
    fun toDomainModel(
        isEnabledOverride: Boolean? = null,
        selectedChapterIdOverride: String? = null,
        preferredModeOverride: StudyMode? = null
    ): TopicItem {
        return TopicItem(
            id = id,
            title = title,
            description = description,
            iconName = iconName,
            isEnabled = isEnabledOverride ?: isEnabledByDefault,
            selectedChapterId = selectedChapterIdOverride?.takeIf { it.isNotBlank() }
                ?: defaultSelectedChapterId,
            supportedModes = supportedModes.map { StudyMode.fromName(it) }
                .ifEmpty { listOf(StudyMode.QUIZ, StudyMode.MEMORY_CARD) },
            preferredStudyMode = preferredModeOverride ?: StudyMode.fromName(preferredStudyMode)
        )
    }
}

data class ChapterDto(
    val id: String,
    val topicId: String,
    val chapterNumber: Int,
    val title: String,
    val description: String
) {
    fun toDomainModel(): ChapterItem {
        return ChapterItem(
            id = id,
            topicId = topicId,
            chapterNumber = chapterNumber,
            title = title,
            description = description
        )
    }
}

data class QuestionDto(
    val id: String,
    val topicId: String,
    val chapterId: String,
    val question: String,
    val codeSnippet: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String,
    val cardFront: String,
    val cardBack: String,
    val cardHint: String,
    val memoryBoxLevel: Int,
    val nextReviewAtMillis: Long
) {
    fun toDomainModel(
        correctCount: Int = 0,
        wrongCount: Int = 0,
        savedMemoryBoxLevel: Int? = null,
        savedNextReviewAtMillis: Long? = null
    ): QuizQuestion {
        return QuizQuestion(
            id = id,
            topicId = topicId,
            chapterId = chapterId,
            question = question,
            codeSnippet = codeSnippet,
            options = options,
            correctOptionIndex = correctOptionIndex,
            explanation = explanation,
            correctCount = correctCount,
            wrongCount = wrongCount,
            cardFront = cardFront.ifBlank { question },
            cardBack = cardBack.ifBlank { options.getOrElse(correctOptionIndex) { "" } },
            cardHint = cardHint,
            memoryBoxLevel = savedMemoryBoxLevel ?: memoryBoxLevel,
            nextReviewAtMillis = savedNextReviewAtMillis ?: nextReviewAtMillis
        )
    }
}

object KnowledgeApiJsonParser {
    fun parseTopicBundle(rawJson: String): TopicBundleResponseDto {
        val root = JSONObject(rawJson)
        val topicObj = root.getJSONObject("topic")

        val supportedModesArr = topicObj.optJSONArray("supported_modes")
        val supportedModes = mutableListOf<String>()
        if (supportedModesArr != null) {
            for (i in 0 until supportedModesArr.length()) {
                supportedModes.add(supportedModesArr.getString(i))
            }
        }

        val topicDto = TopicDto(
            id = topicObj.getString("id"),
            title = topicObj.getString("title"),
            description = topicObj.optString("description", ""),
            iconName = topicObj.optString("icon_name", "Code"),
            isEnabledByDefault = topicObj.optBoolean("is_enabled_by_default", true),
            defaultSelectedChapterId = topicObj.optString("default_selected_chapter_id", ""),
            supportedModes = supportedModes,
            preferredStudyMode = topicObj.optString("preferred_study_mode", "QUIZ")
        )

        val chaptersArr = root.optJSONArray("chapters")
        val chapters = mutableListOf<ChapterDto>()
        if (chaptersArr != null) {
            for (i in 0 until chaptersArr.length()) {
                val c = chaptersArr.getJSONObject(i)
                chapters.add(
                    ChapterDto(
                        id = c.getString("id"),
                        topicId = c.optString("topic_id", topicDto.id),
                        chapterNumber = c.optInt("chapter_number", i + 1),
                        title = c.getString("title"),
                        description = c.optString("description", "")
                    )
                )
            }
        }

        val questionsArr = root.optJSONArray("questions")
        val questions = mutableListOf<QuestionDto>()
        if (questionsArr != null) {
            for (i in 0 until questionsArr.length()) {
                val q = questionsArr.getJSONObject(i)
                val optsArr = q.getJSONArray("options")
                val opts = mutableListOf<String>()
                for (j in 0 until optsArr.length()) {
                    opts.add(optsArr.getString(j))
                }
                questions.add(
                    QuestionDto(
                        id = q.getString("id"),
                        topicId = q.optString("topic_id", topicDto.id),
                        chapterId = q.optString("chapter_id", ""),
                        question = q.getString("question"),
                        codeSnippet = q.optString("code_snippet", ""),
                        options = opts,
                        correctOptionIndex = q.getInt("correct_option_index"),
                        explanation = q.optString("explanation", ""),
                        cardFront = q.optString("card_front", ""),
                        cardBack = q.optString("card_back", ""),
                        cardHint = q.optString("card_hint", ""),
                        memoryBoxLevel = q.optInt("memory_box_level", 1),
                        nextReviewAtMillis = q.optLong("next_review_at_millis", 0L)
                    )
                )
            }
        }

        return TopicBundleResponseDto(
            topic = topicDto,
            chapters = chapters,
            questions = questions
        )
    }
}

