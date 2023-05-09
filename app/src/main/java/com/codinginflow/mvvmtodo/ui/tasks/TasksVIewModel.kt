package com.codinginflow.mvvmtodo.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.codinginflow.mvvmtodo.data.PreferencesManager
import com.codinginflow.mvvmtodo.data.SortOrder
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.TaskDao
import com.codinginflow.mvvmtodo.ui.ADD_TASK_RESULT_OK
import com.codinginflow.mvvmtodo.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

// This is a ViewModel that provides data for the UI to display tasks to the user and respond to user interactions.
// The ViewModelInject annotation is used for dependency injection of the TaskDao, PreferencesManager, and SavedStateHandle objects.
class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao, // Dependency injection of the TaskDao object
    private val preferencesManager: PreferencesManager, // Dependency injection of the PreferencesManager object
    @Assisted private val state: SavedStateHandle // Dependency injection of the SavedStateHandle object
) : ViewModel() {

    // This property holds the search query entered by the user.
    // It is stored in SavedStateHandle to survive configuration changes.
    val searchQuery = state.getLiveData("searchQuery", "")

    // This property holds the preferencesFlow returned by the PreferencesManager.
    val preferencesFlow = preferencesManager.preferencesFlow

    // This property holds a channel to send and receive events related to tasks.
    private val tasksEventChannel = Channel<TasksEvent>()

    // This property holds a flow of events related to tasks.
    // It receives events from the tasksEventChannel and exposes them as a flow to the UI.
    val tasksEvent = tasksEventChannel.receiveAsFlow()

    // This property holds a flow of tasks to display to the user.
    // It combines the searchQuery and preferencesFlow to get the list of tasks to display.
    // It uses flatMapLatest to cancel previous requests when a new request is made.
    val tasksFlow = combine(
        searchQuery.asFlow(), // Converts the LiveData to a Flow
        preferencesFlow // Uses the preferencesFlow property
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    // This property holds a LiveData object that exposes the tasksFlow to the UI.
    val tasks = tasksFlow.asLiveData()

    // This function is called when the user selects a sort order from the menu.
    // It updates the preferencesManager with the new sort order.
    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    // This function is called when the user clicks on the "Hide completed tasks" checkbox.
    // It updates the preferencesManager with the new hideCompleted value.
    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    // This function is called when the user selects a task to edit.
    // It sends a TasksEvent to navigate to the EditTaskScreen with the selected task.
    fun onTaskSelected(task: Task) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToEditTaskScreen(task))
    }

    // This function is called when the user checks or unchecks a task.
    // It updates the task in the database with the new completed value.
    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked))
    }

    // This function is called when the user swipes a task to delete it.
    // It deletes the task from the database and sends a TasksEvent to show an "Undo delete" message.
    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        tasksEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))
    }
    // This function is called when the user clicks the "Undo" button after deleting a task.
    // It inserts the previously deleted task back into the database
    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }
    // This function is called when the user clicks the "Add new task" button.
    // It sends a message to the tasksEventChannel to navigate to the "Add task" screen.
    fun onAddNewTaskClick() = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToAddTaskScreen)
    }
    // This function is a helper function called by onAddEditResult().
    // It sends a message to the tasksEventChannel to show a confirmation message.
    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")
        }
    }

    private fun showTaskSavedConfirmationMessage(text: String) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.ShowTaskSavedConfirmationMessage(text))
    }
    // This function is called when the user clicks the "Delete all completed tasks" button.
    // It sends a message to the tasksEventChannel to navigate to the "Delete all completed tasks" screen.
    fun onDeleteAllCompletedClick() = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToDeleteAllCompletedScreen)
    }
    // This is a sealed class that defines different types of events that can be sent to the tasksEventChannel'.
    // The different types of events are:
    //    NavigateToAddTaskScreen: The user wants to navigate to the "Add task" screen.
    //    NavigateToEditTaskScreen: The user wants to navigate to the "Edit task" screen for a specific task.
    //    ShowUndoDeleteTaskMessage: The user has deleted a task and wants to undo the delete operation.
    //    ShowTaskSavedConfirmationMessage: The user has added or edited a task and wants to see a confirmation message.
    //    NavigateToDeleteAllCompletedScreen: The user wants to navigate to the "Delete all completed tasks" screen.
    sealed class TasksEvent {
        object NavigateToAddTaskScreen : TasksEvent()
        data class NavigateToEditTaskScreen(val task: Task) : TasksEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task) : TasksEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String) : TasksEvent()
        object NavigateToDeleteAllCompletedScreen : TasksEvent()
    }
}