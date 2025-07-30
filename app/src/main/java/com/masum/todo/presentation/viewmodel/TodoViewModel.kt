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
import java.util.Calendar

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
            is TodoUiEvent.UpdateSearchQuery -> updateSearchQuery(event.query)
            is TodoUiEvent.UpdateFilter -> updateFilter(event.filter)
            is TodoUiEvent.UpdateSort -> updateSort(event.sort)
            is TodoUiEvent.ToggleSearchBar -> toggleSearchBar()
            is TodoUiEvent.ToggleFilterOptions -> toggleFilterOptions()
            is TodoUiEvent.ClearSearch -> clearSearch()
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
                        applyFiltersAndSort()
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
        if (task.heading.isBlank()) {
            showError("Task heading cannot be empty")
            return
        }
        
        viewModelScope.launch {
            try {
                repository.updateTask(task)
                hideTaskEditor()
                showSnackbarMessage("Task updated: ${task.heading}")
                ErrorHandler.logError("Task updated successfully: ${task.heading}")
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
    
    private fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFiltersAndSort()
    }
    
    private fun updateFilter(filter: FilterType) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        applyFiltersAndSort()
    }
    
    private fun updateSort(sort: SortType) {
        _uiState.value = _uiState.value.copy(selectedSort = sort)
        applyFiltersAndSort()
    }
    
    private fun toggleSearchBar() {
        _uiState.value = _uiState.value.copy(
            showSearchBar = !_uiState.value.showSearchBar,
            showFilterOptions = false
        )
        if (!_uiState.value.showSearchBar) {
            clearSearch()
        }
    }
    
    private fun toggleFilterOptions() {
        _uiState.value = _uiState.value.copy(
            showFilterOptions = !_uiState.value.showFilterOptions,
            showSearchBar = false
        )
    }
    
    private fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            selectedFilter = FilterType.ALL,
            showSearchBar = false,
            showFilterOptions = false
        )
        applyFiltersAndSort()
    }
    
    private fun applyFiltersAndSort() {
        val tasks = _uiState.value.tasks
        val searchQuery = _uiState.value.searchQuery.lowercase()
        val filter = _uiState.value.selectedFilter
        val sort = _uiState.value.selectedSort
        
        var filteredTasks = tasks
        
        if (searchQuery.isNotBlank()) {
            filteredTasks = filteredTasks.filter { task ->
                task.heading.lowercase().contains(searchQuery) ||
                task.body.lowercase().contains(searchQuery) ||
                task.tags.any { it.lowercase().contains(searchQuery) } ||
                task.subtasks.any { it.title.lowercase().contains(searchQuery) }
            }
        }
        
        filteredTasks = when (filter) {
            FilterType.ALL -> filteredTasks
            FilterType.TODAY -> filteredTasks.filter { task ->
                task.dueDate?.let { dueDate ->
                    val today = Calendar.getInstance()
                    val taskDate = Calendar.getInstance().apply { time = dueDate }
                    today.get(Calendar.YEAR) == taskDate.get(Calendar.YEAR) &&
                    today.get(Calendar.DAY_OF_YEAR) == taskDate.get(Calendar.DAY_OF_YEAR)
                } ?: false
            }
            FilterType.OVERDUE -> filteredTasks.filter { task ->
                task.dueDate?.let { dueDate ->
                    !task.isCompleted && dueDate.before(Date())
                } ?: false
            }
            FilterType.COMPLETED -> filteredTasks.filter { it.isCompleted }
            FilterType.PENDING -> filteredTasks.filter { !it.isCompleted }
            FilterType.HIGH_PRIORITY -> filteredTasks.filter { it.priority == TaskPriority.HIGH }
            FilterType.MEDIUM_PRIORITY -> filteredTasks.filter { it.priority == TaskPriority.MEDIUM }
            FilterType.LOW_PRIORITY -> filteredTasks.filter { it.priority == TaskPriority.LOW }
            FilterType.WITH_DUE_DATE -> filteredTasks.filter { it.dueDate != null }
            FilterType.NO_DUE_DATE -> filteredTasks.filter { it.dueDate == null }
        }
        
        filteredTasks = when (sort) {
            SortType.CREATED_DATE -> filteredTasks.sortedByDescending { it.createdAt }
            SortType.DUE_DATE -> filteredTasks.sortedWith(compareBy<TodoTask> { it.dueDate == null }.thenBy { it.dueDate })
            SortType.PRIORITY -> filteredTasks.sortedBy { it.priority.ordinal }
            SortType.ALPHABETICAL -> filteredTasks.sortedBy { it.heading.lowercase() }
            SortType.COMPLETION_STATUS -> filteredTasks.sortedBy { it.isCompleted }
        }
        
        _uiState.value = _uiState.value.copy(filteredTasks = filteredTasks)
    }
}
