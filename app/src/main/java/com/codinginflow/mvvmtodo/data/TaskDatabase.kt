// Import necessary libraries
package com.codinginflow.mvvmtodo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codinginflow.mvvmtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    // Create an abstract method for getting the TaskDao
    abstract fun taskDao(): TaskDao

    // Create a Callback class for initializing the database with some sample data
    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        // Override the onCreate method to insert sample data into the database
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            // Get the TaskDao instance
            val dao = database.get().taskDao()

            // Launch a coroutine to insert some sample data into the database
            applicationScope.launch {
                dao.insert(Task("Wash the dishes"))
                dao.insert(Task("Do the laundry"))
                dao.insert(Task("Buy groceries", important = true))
                dao.insert(Task("Prepare food", completed = true))
                dao.insert(Task("Call mom"))
                dao.insert(Task("Visit grandma", completed = true))
                dao.insert(Task("Repair my bike"))
                dao.insert(Task("Call Elon Musk"))
            }
        }
    }
}
