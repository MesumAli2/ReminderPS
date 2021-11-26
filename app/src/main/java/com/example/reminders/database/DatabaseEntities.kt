package com.example.reminders.database

import android.view.inspector.IntFlagMapping
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.reminders.domain.ReminderMain

@Entity
data class DatabaseReminders constructor(
    @PrimaryKey()
    val title : String,
    @ColumnInfo(name = "description")
    val description : String,
    @ColumnInfo(name = "todo_data")
    val todoDate: Int
)

fun List<DatabaseReminders>.asDomainModel(): List<ReminderMain>{
    return map {
        ReminderMain(
            title = it.title,
            description = it.description
        )
    }
}


