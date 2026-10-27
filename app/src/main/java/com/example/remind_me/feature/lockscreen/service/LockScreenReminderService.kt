package com.example.remind_me.feature.lockscreen.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.remind_me.MainActivity
import com.example.remind_me.R
import com.example.remind_me.core.localization.getAppStrings
import com.example.remind_me.core.storage.KnowledgeRepository
import com.example.remind_me.feature.lockscreen.ui.LockScreenQuizActivity

class LockScreenReminderService : Service() {

    private lateinit var repository: KnowledgeRepository
    private var isReceiverRegistered = false

    private val screenEventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val action = intent?.action ?: return
            if (action == Intent.ACTION_SCREEN_ON) {
                if (repository.shouldTriggerOnScreenOn()) {
                    launchLockScreenQuiz(context)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        repository = KnowledgeRepository(this)
        createNotificationChannels()
        registerScreenReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_SERVICE) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        val notification = buildForegroundNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID_FOREGROUND,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID_FOREGROUND, notification)
        }
        return START_STICKY
    }

    override fun onDestroy() {
        unregisterScreenReceiver()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun registerScreenReceiver() {
        if (!isReceiverRegistered) {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_USER_PRESENT)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(screenEventReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                registerReceiver(screenEventReceiver, filter)
            }
            isReceiverRegistered = true
        }
    }

    private fun unregisterScreenReceiver() {
        if (isReceiverRegistered) {
            runCatching { unregisterReceiver(screenEventReceiver) }
            isReceiverRegistered = false
        }
    }

    private fun launchLockScreenQuiz(context: Context) {
        val quizIntent = LockScreenQuizActivity.createIntent(context)
        runCatching {
            context.startActivity(quizIntent)
        }

        // Also send a full-screen intent notification to ensure lock screen presentation on strict OEMs
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE_QUIZ,
            quizIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val strings = getAppStrings(repository.getSettings().languageCode)
        val payload = repository.getNextLockScreenQuestion()
        val questionPreview = payload?.question?.question ?: strings.notificationDefaultQuestionPreview

        val quizNotification = NotificationCompat.Builder(context, CHANNEL_ID_QUIZ_ALERT)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(strings.notificationQuizAlertTitle)
            .setContentText(questionPreview)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(fullScreenPendingIntent)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID_QUIZ_ALERT, quizNotification)
    }

    private fun buildForegroundNotification(): Notification {
        val strings = getAppStrings(repository.getSettings().languageCode)
        val mainIntent = Intent(this, MainActivity::class.java)
        val mainPendingIntent = PendingIntent.getActivity(
            this,
            0,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val testQuizIntent = LockScreenQuizActivity.createIntent(this)
        val testQuizPendingIntent = PendingIntent.getActivity(
            this,
            1,
            testQuizIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID_SERVICE)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(strings.notificationServiceTitle)
            .setContentText(strings.notificationServiceDesc)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(mainPendingIntent)
            .addAction(0, strings.notificationActionReviewNow, testQuizPendingIntent)
            .build()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val strings = getAppStrings(repository.getSettings().languageCode)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val serviceChannel = NotificationChannel(
                CHANNEL_ID_SERVICE,
                strings.notificationChannelServiceName,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = strings.notificationChannelServiceDesc
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val quizAlertChannel = NotificationChannel(
                CHANNEL_ID_QUIZ_ALERT,
                strings.notificationChannelQuizName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = strings.notificationChannelQuizDesc
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            manager.createNotificationChannel(serviceChannel)
            manager.createNotificationChannel(quizAlertChannel)
        }
    }

    companion object {
        private const val CHANNEL_ID_SERVICE = "remind_me_lockscreen_service_channel"
        private const val CHANNEL_ID_QUIZ_ALERT = "remind_me_lockscreen_quiz_channel"
        private const val NOTIFICATION_ID_FOREGROUND = 1001
        private const val NOTIFICATION_ID_QUIZ_ALERT = 1002
        private const val REQUEST_CODE_QUIZ = 2001
        const val ACTION_STOP_SERVICE = "com.example.remind_me.ACTION_STOP_SERVICE"

        fun start(context: Context) {
            val intent = Intent(context, LockScreenReminderService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, LockScreenReminderService::class.java)
            context.stopService(intent)
        }
    }
}

