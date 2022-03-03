package com.mesum.reminders.repository

import android.graphics.Bitmap
import android.view.View
import androidx.camera.core.ImageCapture
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import com.mesum.reminders.database.*
import com.mesum.reminders.domain.ReminderMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.coroutineContext

class RemindersRepo (private val database : ReminderDatabase ) {

    var reminders : LiveData<List<ReminderMain>> = Transformations.map(database.ReminderDao().getReminders()) {
        it.asDomainModel()
    }


     fun getREminderswithimages(remindertitle: String) :LiveData<RemindersWithImages> {
          return  database.ReminderDao().getReminderswithImages(remindertitle)
    }
    suspend fun insertReminder(reminder: DatabaseReminders){
            withContext(Dispatchers.IO){
                database.ReminderDao().insertReminder(reminder)
            }
        }

    suspend fun insertImages(images : ImagesEntitys){
        withContext(Dispatchers.IO){
            database.ReminderDao().insertImages(images)
        }
    }

        suspend fun deleteReminder(reminder: DatabaseReminders){
            withContext(Dispatchers.IO){
                database.ReminderDao().delete(reminder)
            }
        }

    fun getReminder(reminderID: String): LiveData<DatabaseReminders> {
        return database.ReminderDao().retrieveReminder(reminderID).asLiveData()
    }
    //method invoked from search bar
    fun getMultiReminders(reminderID: String): LiveData<List<ReminderMain>> {
        return  Transformations.map(database.ReminderDao().retrieveMultiReminders(reminderID).asLiveData()){
            it.asDomainModel()
        }
    }


    fun getReminderListWithImages(): LiveData<List<RemindersWithImages>>{
      return  database.ReminderDao().getRemindersImageList().asLiveData()
    }



    suspend fun completeTask(reminderid: String, completed: Boolean){
        withContext(Dispatchers.IO){
            database.ReminderDao().completedReminder(reminderid,completed )
        }
    }


}