package com.example.remind_me.feature.lockscreen.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.remind_me.core.storage.KnowledgeRepository
import com.example.remind_me.feature.lockscreen.service.LockScreenReminderService

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action == Intent.ACTION_BOOT_COMPLETED || action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            val repository = KnowledgeRepository(context)
            if (repository.getSettings().isServiceEnabled) {
                LockScreenReminderService.start(context)
            }
        }
    }
}

