package com.codinginflow.mvvmtodo.data

// Import dependencies
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

// Define an Entity for Room Database with table name "task_table"
@Entity(tableName = "task_table")
// Make this class Parcelable using the Parcelize annotation
@Parcelize
// Define a data class named Task
data class Task(
    // Declare the properties of the Task class
    val name: String, // A string representing the name of the task
    val important: Boolean = false, // A boolean representing if the task is important or not
    val completed: Boolean = false, // A boolean representing if the task is completed or not
    val created: Long = System.currentTimeMillis(), // A long representing the time when the task was created
    @PrimaryKey(autoGenerate = true) val id: Int = 0 // An integer representing the ID of the task, with auto-generate enabled

) : Parcelable {
    // Define a computed property to format the created date
    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(created)

}