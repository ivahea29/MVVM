package com.codinginflow.mvvmtodo.ui.deleteallcompleted

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.codinginflow.mvvmtodo.data.TaskDao
import com.codinginflow.mvvmtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteAllCompletedViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao, // TaskDao object to perform database operations
    @ApplicationScope private val applicationScope: CoroutineScope // CoroutineScope used to launch a coroutine
) : ViewModel() {

    // This function is called when the user confirms the deletion of all completed tasks
    fun onConfirmClick() = applicationScope.launch {
        taskDao.deleteCompletedTasks() // Delete all completed tasks from the database using the taskDao object
    }
}
