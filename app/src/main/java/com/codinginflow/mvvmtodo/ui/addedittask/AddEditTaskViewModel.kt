package com.codinginflow.mvvmtodo.ui.addedittask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.TaskDao
import com.codinginflow.mvvmtodo.ui.ADD_TASK_RESULT_OK
import com.codinginflow.mvvmtodo.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditTaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,  // Dependency injection for TaskDao using Hilt
    @Assisted private val state: SavedStateHandle // Hilt automatically provides the SavedStateHandle object
) : ViewModel() {

    val task = state.get<Task>("task") // Get the task from the SavedStateHandle

    // Set the task name and importance variables based on the SavedStateHandle or the task object
    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            state.set("taskName", value) // Save the new value in the SavedStateHandle
        }

    var taskImportance = state.get<Boolean>("taskImportance") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("taskImportance", value) // Save the new value in the SavedStateHandle
        }

    private val addEditTaskEventChannel = Channel<AddEditTaskEvent>() // Create a channel for communication with the fragment
    val addEditTaskEvent = addEditTaskEventChannel.receiveAsFlow() // Get the channel as a flow

    fun onSaveClick() {
        if (taskName.isBlank()) { // Check if the task name is empty
            showInvalidInputMessage("Name cannot be empty")
            return
        }

        if (task != null) { // If there is an existing task, update it
            val updatedTask = task.copy(name = taskName, important = taskImportance)
            updateTask(updatedTask)
        } else { // Otherwise, create a new task
            val newTask = Task(name = taskName, important = taskImportance)
            createTask(newTask)
        }
    }
    // Functions for creating or updating a task in the database
    private fun createTask(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updateTask(task: Task) = viewModelScope.launch {
        taskDao.update(task)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }
    // Function for showing an error message in the fragment
    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        addEditTaskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(text))
    }
    // Sealed class for communication between the ViewModel and the fragment
    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditTaskEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditTaskEvent()
    }
}