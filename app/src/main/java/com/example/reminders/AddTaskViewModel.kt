package com.example.reminders

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.reminders.database.DatabaseReminders
import com.example.reminders.database.ReminderDatabase
import com.example.reminders.database.asDomainModel
import com.example.reminders.domain.ReminderMain
import com.example.reminders.repository.RemindersRepo
import com.example.reminders.util.Event
import kotlinx.coroutines.launch
import java.lang.Exception

class AddTaskViewModel(application: Application) : ViewModel() {

    private val repository = RemindersRepo(ReminderDatabase.getDatabase(application))

    val reminders = repository.reminders

    private fun TransformMain(reminder: ReminderMain): DatabaseReminders{
        return DatabaseReminders(title = reminder.title, description = reminder.description, todoDate = 23)

    }
    private val _openTaskEvent = MutableLiveData<Event<ReminderMain>>()
    val openTaskEvent: LiveData<Event<ReminderMain>> = _openTaskEvent
    init {
        repository.reminders

    }

    fun getReminder(reminderId: String){
    viewModelScope.launch {
        repository.getReminder(reminderId)
    }
        }

    fun insertItem(title: String, description: String){
        val reminders = ReminderMain(title = title, description = description)
        addReminders(reminders)
    }

   private fun addReminders(reminder : ReminderMain){
        viewModelScope.launch {
            repository.insertReminder(TransformMain(reminder))
        }
    }

    fun deleteItem(reminder: ReminderMain){
        viewModelScope.launch {
            repository.deleteReminder(TransformMain(reminder))
        }
    }
    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    fun openTask(taskId: ReminderMain) {
        _openTaskEvent.value = Event(taskId)
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