package com.masum.todo.presentation.viewmodel

import com.masum.todo.domain.model.TodoTask

data class TodoUiState(
    val tasks: List<TodoTask> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val currentTaskHeading: String = "",
    val currentTaskBody: String = "",
    val taskToEdit: TodoTask? = null,
    val snackbarMessage: String? = null
)

sealed class TodoUiEvent {
    data object LoadTasks : TodoUiEvent()
    data class AddTask(val heading: String, val body: String = "") : TodoUiEvent()
    data class UpdateTask(val task: TodoTask) : TodoUiEvent()
    data class DeleteTask(val task: TodoTask) : TodoUiEvent()
    data class ToggleTaskCompletion(val task: TodoTask, val isCompleted: Boolean) : TodoUiEvent()
    data object ShowAddDialog : TodoUiEvent()
    data object HideAddDialog : TodoUiEvent()
    data class ShowEditDialog(val task: TodoTask) : TodoUiEvent()
    data object HideEditDialog : TodoUiEvent()
    data class UpdateCurrentTaskHeading(val heading: String) : TodoUiEvent()
    data class UpdateCurrentTaskBody(val body: String) : TodoUiEvent()
    data object ClearSnackbarMessage : TodoUiEvent()
    data class ShowError(val message: String) : TodoUiEvent()
}
