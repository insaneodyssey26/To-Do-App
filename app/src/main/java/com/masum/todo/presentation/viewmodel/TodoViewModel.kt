package com.masum.todo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masum.todo.domain.model.TaskColor
import com.masum.todo.domain.model.TaskPriority
import com.masum.todo.domain.model.TodoTask
import com.masum.todo.domain.model.Subtask
import com.masum.todo.domain.repository.TodoRepository
import com.masum.todo.utils.ErrorHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Date

class TodoViewModel(
    private val repository: TodoRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()
    
    init {
        loadTasks()
    }
    
    fun onEvent(event: TodoUiEvent) {
        when (event) {
            is TodoUiEvent.LoadTasks -> loadTasks()
            is TodoUiEvent.AddTask -> addTask(event.heading, event.body)
            is TodoUiEvent.AddAdvancedTask -> addAdvancedTask(
                event.heading, event.body, event.color, event.priority,
                event.dueDate, event.subtasks, event.tags
            )
            is TodoUiEvent.UpdateTask -> updateTask(event.task)
            is TodoUiEvent.DeleteTask -> deleteTask(event.task)
            is TodoUiEvent.ToggleTaskCompletion -> toggleTaskCompletion(event.task, event.isCompleted)
            is TodoUiEvent.ShowAddDialog -> showAddDialog()
            is TodoUiEvent.HideAddDialog -> hideAddDialog()
            is TodoUiEvent.ShowTaskEditor -> showTaskEditor()
            is TodoUiEvent.HideTaskEditor -> hideTaskEditor()
            is TodoUiEvent.ShowEditDialog -> showEditDialog(event.task)
            is TodoUiEvent.ShowTaskEditorForEdit -> showTaskEditorForEdit(event.task)
            is TodoUiEvent.HideEditDialog -> hideEditDialog()
            is TodoUiEvent.UpdateCurrentTaskHeading -> updateCurrentTaskHeading(event.heading)
            is TodoUiEvent.UpdateCurrentTaskBody -> updateCurrentTaskBody(event.body)
            is TodoUiEvent.UpdateCurrentTaskColor -> updateCurrentTaskColor(event.color)
            is TodoUiEvent.ClearSnackbarMessage -> clearSnackbarMessage()
            is TodoUiEvent.ShowError -> showError(event.message)
            is TodoUiEvent.ToggleViewMode -> toggleViewMode()
        }
    }
    
    private fun loadTasks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                repository.getAllTasks()
                    .catch { throwable ->
                        ErrorHandler.logError("Failed to load tasks", throwable)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = ErrorHandler.getErrorMessage(throwable)
                        )
                    }
                    .collect { tasks ->
                        _uiState.value = _uiState.value.copy(
                            tasks = tasks,
                            isLoading = false,
                            error = null
                        )
                    }
            } catch (e: Exception) {
                ErrorHandler.logError("Unexpected error while loading tasks", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = ErrorHandler.getErrorMessage(e)
                )
            }
        }
    }
    
    private fun addTask(heading: String, body: String) {
        if (heading.isBlank()) {
            showError("Task heading cannot be empty")
            return
        }
        
        viewModelScope.launch {
            try {
                val newTask = TodoTask(
                    heading = heading.trim(),
                    body = body.trim(),
                    color = _uiState.value.currentTaskColor
                )
                repository.insertTask(newTask)
                hideAddDialog()
                clearCurrentTask()
                showSnackbarMessage("Task added: ${newTask.heading}")
                ErrorHandler.logError("Task added successfully: ${newTask.heading}")
            } catch (e: Exception) {
                ErrorHandler.logError("Failed to add task", e)
                showError("Failed to add task: ${ErrorHandler.getErrorMessage(e)}")
            }
        }
    }
    
    private fun updateTask(task: TodoTask) {
        val heading = _uiState.value.currentTaskHeading
        val body = _uiState.value.currentTaskBody
        val color = _uiState.value.currentTaskColor
        
        if (heading.isBlank()) {
            showError("Task heading cannot be empty")
            return
        }
        
        viewModelScope.launch {
            try {
                val updatedTask = task.copy(
                    heading = heading.trim(),
                    body = body.trim(),
                    color = color
                )
                repository.updateTask(updatedTask)
                hideEditDialog()
                clearCurrentTask()
                showSnackbarMessage("Task updated: ${updatedTask.heading}")
                ErrorHandler.logError("Task updated successfully: ${updatedTask.heading}")
            } catch (e: Exception) {
                ErrorHandler.logError("Failed to update task", e)
                showError("Failed to update task: ${ErrorHandler.getErrorMessage(e)}")
            }
        }
    }
    
    private fun deleteTask(task: TodoTask) {
        viewModelScope.launch {
            try {
                repository.deleteTask(task)
                showSnackbarMessage("Task deleted: ${task.heading}")
                ErrorHandler.logError("Task deleted successfully: ${task.heading}")
            } catch (e: Exception) {
                ErrorHandler.logError("Failed to delete task", e)
                showError("Failed to delete task: ${ErrorHandler.getErrorMessage(e)}")
            }
        }
    }
    
    private fun toggleTaskCompletion(task: TodoTask, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                val updatedTask = task.copy(isCompleted = isCompleted)
                repository.updateTask(updatedTask)
                val status = if (isCompleted) "completed" else "marked active"
                showSnackbarMessage("Task $status: ${task.heading}")
                ErrorHandler.logError("Task completion toggled: ${task.heading} - $status")
            } catch (e: Exception) {
                ErrorHandler.logError("Failed to update task completion", e)
                showError("Failed to update task: ${ErrorHandler.getErrorMessage(e)}")
            }
        }
    }
    
    private fun addAdvancedTask(
        heading: String,
        body: String,
        color: TaskColor,
        priority: TaskPriority,
        dueDate: Date?,
        subtasks: List<Subtask>,
        tags: List<String>
    ) {
        if (heading.isBlank()) {
            showError("Task heading cannot be empty")
            return
        }
        
        viewModelScope.launch {
            try {
                val newTask = TodoTask(
                    heading = heading.trim(),
                    body = body.trim(),
                    color = color,
                    priority = priority,
                    dueDate = dueDate,
                    subtasks = subtasks,
                    tags = tags
                )
                repository.insertTask(newTask)
                hideTaskEditor()
                showSnackbarMessage("Task created: ${newTask.heading}")
                ErrorHandler.logError("Advanced task added successfully: ${newTask.heading}")
            } catch (e: Exception) {
                ErrorHandler.logError("Failed to add advanced task", e)
                showError("Failed to create task: ${ErrorHandler.getErrorMessage(e)}")
            }
        }
    }
    
    private fun showTaskEditor() {
        _uiState.value = _uiState.value.copy(showTaskEditor = true)
    }
    
    private fun hideTaskEditor() {
        _uiState.value = _uiState.value.copy(
            showTaskEditor = false,
            taskToEdit = null
        )
    }
    
    private fun showTaskEditorForEdit(task: TodoTask) {
        _uiState.value = _uiState.value.copy(
            showTaskEditor = true,
            taskToEdit = task
        )
    }
    
    private fun showAddDialog() {
        _uiState.value = _uiState.value.copy(
            showAddDialog = true,
            currentTaskHeading = "",
            currentTaskBody = "",
            currentTaskColor = TaskColor.DEFAULT
        )
    }
    
    private fun hideAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = false)
        clearCurrentTask()
    }
    
    private fun showEditDialog(task: TodoTask) {
        _uiState.value = _uiState.value.copy(
            showEditDialog = true,
            taskToEdit = task,
            currentTaskHeading = task.heading,
            currentTaskBody = task.body,
            currentTaskColor = task.color
        )
    }
    
    private fun hideEditDialog() {
        _uiState.value = _uiState.value.copy(
            showEditDialog = false,
            taskToEdit = null
        )
        clearCurrentTask()
    }
    
    private fun updateCurrentTaskHeading(heading: String) {
        _uiState.value = _uiState.value.copy(currentTaskHeading = heading)
    }
    
    private fun updateCurrentTaskBody(body: String) {
        _uiState.value = _uiState.value.copy(currentTaskBody = body)
    }
    
    private fun updateCurrentTaskColor(color: TaskColor) {
        _uiState.value = _uiState.value.copy(currentTaskColor = color)
    }
    
    private fun clearCurrentTask() {
        _uiState.value = _uiState.value.copy(
            currentTaskHeading = "",
            currentTaskBody = "",
            currentTaskColor = TaskColor.DEFAULT
        )
    }
    
    private fun showSnackbarMessage(message: String) {
        _uiState.value = _uiState.value.copy(snackbarMessage = message)
    }
    
    private fun clearSnackbarMessage() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }
    
    private fun showError(message: String) {
        _uiState.value = _uiState.value.copy(
            error = message,
            snackbarMessage = message
        )
    }
    
    private fun toggleViewMode() {
        _uiState.value = _uiState.value.copy(
            isGridView = !_uiState.value.isGridView
        )
    }
}
