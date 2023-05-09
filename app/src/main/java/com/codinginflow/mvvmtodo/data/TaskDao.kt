package com.codinginflow.mvvmtodo.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // Returns a Flow of tasks based on the search query, sort order, and hide completed status
    fun getTasks(query: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<Task>> =
        // Depending on the sort order, call the corresponding method to retrieve tasks
        when (sortOrder) {
            SortOrder.BY_DATE -> getTasksSortedByDateCreated(query, hideCompleted)
            SortOrder.BY_NAME -> getTasksSortedByName(query, hideCompleted)
        }

    // Returns a Flow of tasks sorted by name, with the option to hide completed tasks
    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC, name")
    fun getTasksSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    // Returns a Flow of tasks sorted by date created, with the option to hide completed tasks
    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC, created")
    fun getTasksSortedByDateCreated(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    // Inserts a new task into the database, replacing any existing task with the same ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    // Updates an existing task in the database
    @Update
    suspend fun update(task: Task)

    // Deletes a task from the database
    @Delete
    suspend fun delete(task: Task)

    // Deletes all completed tasks from the database
    @Query("DELETE FROM task_table WHERE completed = 1")
    suspend fun deleteCompletedTasks()
}
