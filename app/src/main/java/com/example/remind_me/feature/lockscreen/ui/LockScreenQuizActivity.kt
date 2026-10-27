package com.example.remind_me.feature.lockscreen.ui

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.remind_me.core.localization.LocalAppStrings
import com.example.remind_me.core.localization.getAppStrings
import com.example.remind_me.core.storage.KnowledgeRepository
import com.example.remind_me.core.theme.RemindmeTheme

class LockScreenQuizActivity : ComponentActivity() {

    private lateinit var repository: KnowledgeRepository
    private var canDismissBack: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureLockScreenWindow()
        enableEdgeToEdge()

        repository = KnowledgeRepository(this)
        val initialSettings = repository.getSettings()
        canDismissBack = !initialSettings.requireCorrectAnswerToDismiss

        val filterTopicId = intent?.getStringExtra(EXTRA_TOPIC_ID)
        val filterChapterId = intent?.getStringExtra(EXTRA_CHAPTER_ID)

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (canDismissBack) {
                        dismissAndFinish()
                    }
                }
            }
        )

        setContent {
            RemindmeTheme {
                var currentPayload by remember {
                    mutableStateOf(
                        repository.getNextLockScreenQuestion(
                            filterTopicId = filterTopicId,
                            filterChapterId = filterChapterId
                        )
                    )
                }
                var settings by remember { mutableStateOf(repository.getSettings()) }
                val appStrings = remember(settings.languageCode) {
                    getAppStrings(settings.languageCode)
                }

                CompositionLocalProvider(LocalAppStrings provides appStrings) {
                    val payload = currentPayload
                    if (payload == null) {
                        LaunchedEffect(Unit) {
                            dismissAndFinish()
                        }
                    } else {
                        LockScreenQuizScreen(
                            question = payload.question,
                            topic = payload.topic,
                            chapter = payload.chapter,
                            streak = settings.currentStreak,
                            requireCorrectToDismiss = settings.requireCorrectAnswerToDismiss,
                            onAnswerSubmitted = { isCorrect ->
                                repository.recordAnswer(payload.question.id, isCorrect)
                                settings = repository.getSettings()
                                if (isCorrect) {
                                    canDismissBack = true
                                }
                            },
                            onNextQuestion = {
                                canDismissBack = !settings.requireCorrectAnswerToDismiss
                                currentPayload = repository.getNextLockScreenQuestion(
                                    filterTopicId = filterTopicId,
                                    filterChapterId = filterChapterId
                                )
                            },
                            onDismissLockScreen = {
                                dismissAndFinish()
                            }
                        )
                    }
                }
            }
        }
    }

    private fun configureLockScreenWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun dismissAndFinish() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
            keyguardManager?.requestDismissKeyguard(this, null)
        }
        finish()
    }

    companion object {
        private const val EXTRA_TOPIC_ID = "extra_topic_id"
        private const val EXTRA_CHAPTER_ID = "extra_chapter_id"

        fun createIntent(
            context: Context,
            topicId: String? = null,
            chapterId: String? = null
        ): Intent {
            return Intent(context, LockScreenQuizActivity::class.java).apply {
                if (!topicId.isNullOrBlank()) putExtra(EXTRA_TOPIC_ID, topicId)
                if (!chapterId.isNullOrBlank()) putExtra(EXTRA_CHAPTER_ID, chapterId)
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                )
            }
        }
    }
}
