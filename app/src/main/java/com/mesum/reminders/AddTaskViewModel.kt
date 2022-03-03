package com.mesum.reminders

import android.app.Activity
import android.app.Application
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.mesum.reminders.database.DatabaseReminders
import com.mesum.reminders.database.ImagesEntitys
import com.mesum.reminders.database.ReminderDatabase
import com.mesum.reminders.database.RemindersWithImages
import com.mesum.reminders.domain.ReminderMain
import com.mesum.reminders.repository.RemindersRepo
import com.mesum.reminders.util.Event
import kotlinx.coroutines.launch
import java.util.*

class AddTaskViewModel(application: Application) : ViewModel() {

    private val repository = RemindersRepo(ReminderDatabase.getDatabase(application))
    var everyDay = "ones"
    var ringerFrequency = "Ring once"
    val reminders = repository.reminders
    var reminderType : Boolean = false
    var paramsCounter = 5;
    var reminderimageslist = mutableListOf<Uri>()
    private val _openTaskEvent = MutableLiveData<Event<ReminderMain>>()
    val openTaskEvent: LiveData<Event<ReminderMain>> = _openTaskEvent
    init {
        repository.reminders
    }
    var currentWorker = MutableLiveData<String>()
    val appSettingsPreferences = application.getSharedPreferences("appSettings", Application.MODE_PRIVATE)
    val editSharePref =appSettingsPreferences.edit()
    val isNightModeOn = appSettingsPreferences.getBoolean("NightMode",false )

    fun  reminderwithimages(reminderTitle: String):LiveData<RemindersWithImages>{
              return  repository.getREminderswithimages(reminderTitle)

    }

    fun reminderListWithImages(): LiveData<List<RemindersWithImages>>{
      return  repository.getReminderListWithImages()
    }

    fun getReminder(reminderId: String): LiveData<DatabaseReminders>{
        return repository.getReminder(reminderId)
    }

    fun getMultiReminder(reminderTitle: String): LiveData<List<ReminderMain>>{
        return repository.getMultiReminders(reminderTitle)
    }
    fun getRealPathFromUri(context: Context, contentUri: Uri?): String {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.getContentResolver().query(contentUri!!, proj, null, null, null)
            val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }

    fun insertItem(title: String, description: String, date: String, workerID: UUID, frequency: String,ringerFrequency: String){
        val reminders = DatabaseReminders(title = title, description = description, todoDate = date, workerID =workerID.toString(),completedTask = false, frequency = frequency,  ringPattern =ringerFrequency )
        addReminders(reminders)
    }

    fun insertImages(title: String, images : String){
        val image = ImagesEntitys(title = title, images = images)
        Log.d("Inserted",images )
        addimageslist(image)
    }
   private fun addReminders(reminder : DatabaseReminders){
        viewModelScope.launch {
            repository.insertReminder(reminder)
        }
    }
    private fun addimageslist(images : ImagesEntitys){
        viewModelScope.launch {
            repository.insertImages(images)
        }
    }


    //Updates the value of current worker to be deleted
    fun deleteItem(reminder: DatabaseReminders){
        viewModelScope.launch {
          repository.deleteReminder(reminder)
        }
        currentWorker.value = reminder.workerID
    }



    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    fun openTask(taskId: ReminderMain) {
        _openTaskEvent.value = Event(taskId)
    }

    fun completeTask(reminderTitle : String, completed: Boolean) = viewModelScope.launch {
    repository.completeTask(reminderTitle, completed)
    }


     fun hideKeyboard(activity: Activity) {
        val inputManager = activity
            .getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager

        // check if no view has focus:
        val currentFocusedView = activity.currentFocus
        //if theres is keyboard
        if (currentFocusedView != null) {
            //hides the keyboard
            inputManager.hideSoftInputFromWindow(
                currentFocusedView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }


    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddTaskViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddTaskViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}