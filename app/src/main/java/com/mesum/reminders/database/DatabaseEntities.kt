package com.mesum.reminders.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import com.mesum.reminders.domain.ReminderMain
@Entity
data class DatabaseReminders constructor(
    @PrimaryKey()
    val title : String,
    @ColumnInfo(name = "description")
    val description : String,
    @ColumnInfo(name = "todo_data")
    val todoDate: String,
    @ColumnInfo(name = "work_request")
    val workerID : String,
    @ColumnInfo(name = "completed")
    val completedTask: Boolean,
    @ColumnInfo(name = "frequency")
    val frequency: String,
    @ColumnInfo(name = "ringPattern")
    val ringPattern: String

){

    val titleForList: String
        get() = if (title.isNotEmpty()) title else description


    val isActive
        get() = !completedTask

    val isEmpty
        get() = title.isEmpty() || description.isEmpty()
}


@Entity
data class ImagesEntitys(
    @PrimaryKey()
    val images : String,
    val title: String?
)

fun List<DatabaseReminders>.asDomainModel(): List<ReminderMain>{
    return map {
        ReminderMain(
            title = it.title,
            description = it.description,
            date = it.todoDate,
            workerID = it.workerID,
            isCompleted = it.completedTask,
            frequency = it.frequency,
            ringpattern = it.ringPattern
        )
    }
}


