package com.example.reminders.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.concurrent.Flow

@Dao
interface ReminderDao{
@Query( "Select * from DatabaseReminders")
fun getReminders(): LiveData<List<DatabaseReminders>>



@Insert(onConflict = OnConflictStrategy.REPLACE)
fun insertReminder(reminders: DatabaseReminders)

@Query("Select * from DatabaseReminders where title = :reminderID ")
fun retrieveReminder(reminderID: String) : DatabaseReminders

@Delete()
fun delete(reminders: DatabaseReminders)
}

@Database(entities = [DatabaseReminders::class], version= 1, exportSchema = false)
abstract class ReminderDatabase:RoomDatabase(){
    //To access dao methods
    abstract fun ReminderDao(): ReminderDao

    companion object{
        @Volatile
        private var INSTANCE : ReminderDatabase? = null
        fun getDatabase(context: Context): ReminderDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReminderDatabase::class.java,

                    "Reminderss"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}