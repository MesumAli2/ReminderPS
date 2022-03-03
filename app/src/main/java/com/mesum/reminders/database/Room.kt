package com.mesum.reminders.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao{
//Gets all the reminders
@Query( "Select * from DatabaseReminders")
fun getReminders(): LiveData<List<DatabaseReminders>>


//Insert new reminder into the database
@Insert(onConflict = OnConflictStrategy.REPLACE)
fun insertReminder(reminders: DatabaseReminders)

@Insert(onConflict = OnConflictStrategy.IGNORE)
fun insertImages(images : ImagesEntitys)

//Used in reminder detail fragment to get the clicked reminder data
@Query("Select * from DatabaseReminders where title = :reminderID ")
fun retrieveReminder(reminderID: String) :Flow<DatabaseReminders>

//User in search view to filter reminder
@Query("Select * from DatabaseReminders where title LIKE '%' || :reminderID || '%'")
fun retrieveMultiReminders(reminderID: String) :Flow<List<DatabaseReminders>>

@Delete()
fun delete(reminders: DatabaseReminders)

//Query used to edit the current reminder
@Query("UPDATE DatabaseReminders SET completed = :completed WHERE title = :reminderID ")
fun completedReminder(reminderID: String,completed: Boolean)

    @Transaction
    @Query("SELECT * FROM DatabaseReminders where title = :title ")
    fun getReminderswithImages(title : String): LiveData<RemindersWithImages>

    @Transaction
    @Query("SELECT * FROM DatabaseReminders  ")
    fun getRemindersImageList(): Flow<List<RemindersWithImages>>


}

@Database(entities = [DatabaseReminders::class, ImagesEntitys::class], version= 2, exportSchema = false)
abstract class ReminderDatabase:RoomDatabase(){
    //To access dao methods
    abstract fun ReminderDao(): ReminderDao

    companion object{
        @Volatile
        private var INSTANCE : ReminderDatabase? = null

        val migration1_2 = object : Migration(1, 2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE DatabaseReminders add ringPattern TEXT not null DEFAULT('Ring once')")
            }

        }

        fun getDatabase(context: Context): ReminderDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReminderDatabase::class.java,

                    "Reminderssswssssd"
                )   .addMigrations(migration1_2)
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}