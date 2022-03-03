package com.mesum.reminders.util

import android.content.Context
import androidx.work.WorkerParameters
import android.media.RingtoneManager

import androidx.work.CoroutineWorker
import kotlinx.coroutines.delay


class ReminderWorker(val context : Context, private val params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {

    NotificationHelper(context).createNotification(
        inputData.getString("title").toString()
        , inputData.getString("message").toString())
        playRingtone()
       return  Result.success()

    }

     suspend fun playRingtone(){
        val ringtone by lazy { RingtoneManager.getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)) }
        ringtone.play()
         delay(8000)
        ringtone.stop()
    }
}