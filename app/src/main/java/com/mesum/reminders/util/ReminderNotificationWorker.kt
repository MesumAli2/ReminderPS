package com.mesum.reminders.util

import android.content.Context
import android.media.RingtoneManager
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class ReminderNotificationWorker (val context : Context, private val params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): ListenableWorker.Result {

        NotificationHelper(context).createNotification(
            inputData.getString("title").toString()
            , inputData.getString("message").toString())
        return  ListenableWorker.Result.success()

    }


}