package com.mesum.reminders.domain

data class ReminderMain (
    val title : String,
    val description: String,
    val date: String,
    val workerID:String,
    var isCompleted: Boolean,
    var frequency: String,
    var ringpattern: String,
        )
{

    val titleForList: String
        get() = if (title.isNotEmpty()) title else description



    val isActive
        get() = !isCompleted

    val isEmpty
        get() = title.isEmpty() || description.isEmpty()
}

