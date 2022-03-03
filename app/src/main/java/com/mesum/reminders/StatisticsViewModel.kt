package com.mesum.reminders

import android.app.Application
import androidx.lifecycle.*
import com.mesum.reminders.database.ReminderDatabase
import com.mesum.reminders.domain.ReminderMain
import com.mesum.reminders.repository.RemindersRepo
import com.mesum.reminders.util.StatsResult
import com.mesum.reminders.util.getActiveAndCompletedStats

/**
 * ViewModel for the statistics screen.
 */
class StatisticsViewModel(application: Application) : ViewModel() {

    //returns the reminders database in main model format
    private val reminderRepository = RemindersRepo(ReminderDatabase.getDatabase(application))

    private val reminders: LiveData<List<ReminderMain>> = reminderRepository.reminders
    private val _dataLoading = MutableLiveData<Boolean>(false)
     val stats: LiveData<StatsResult> = reminders.map {
        getActiveAndCompletedStats(it)
    }

    val activeTasksPercent: LiveData<Float> = stats.map { it.activeTasksPercent ?: 0f }
    val completedTasksPercent: LiveData<Float> = stats.map { it.completedTasksPercent ?: 0f }
    val dataLoading: LiveData<Boolean> = _dataLoading
    val error: LiveData<Boolean> = reminders.map { it is Error }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return StatisticsViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
