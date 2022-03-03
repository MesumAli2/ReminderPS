package com.mesum.reminders.database

import android.media.Image
import androidx.room.Embedded
import androidx.room.Relation

data class RemindersWithImages (
    @Embedded val reminders : DatabaseReminders,
    @Relation (
        parentColumn = "title",
        entityColumn = "title"
            )

    val images : List<ImagesEntitys>
)