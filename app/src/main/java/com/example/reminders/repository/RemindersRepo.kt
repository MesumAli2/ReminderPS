package com.example.reminders.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.reminders.database.DatabaseReminders
import com.example.reminders.database.ReminderDatabase
import com.example.reminders.database.asDomainModel
import com.example.reminders.domain.ReminderMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.Flow

class RemindersRepo (private val database : ReminderDatabase ) {

    var reminders : LiveData<List<ReminderMain>> = Transformations.map(database.ReminderDao().getReminders()) {
        it.asDomainModel()
    }



        suspend fun insertReminder(reminder: DatabaseReminders){
            withContext(Dispatchers.IO){
                database.ReminderDao().insertReminder(reminder)
            }
        }

        suspend fun deleteReminder(reminder: DatabaseReminders){
            withContext(Dispatchers.IO){
                database.ReminderDao().delete(reminder)
            }
        }

        suspend fun getReminder(reminderID : String): DatabaseReminders{
            var reminders :DatabaseReminders
            withContext(Dispatchers.IO){
                reminders = database.ReminderDao().retrieveReminder(reminderID)
            }
            return  reminders
        }


}