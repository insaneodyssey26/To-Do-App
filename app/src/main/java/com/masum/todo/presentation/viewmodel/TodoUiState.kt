package com.masum.todo.presentation.viewmodel

import com.masum.todo.domain.model.TaskColor
import com.masum.todo.domain.model.TaskPriority
import com.masum.todo.domain.model.TodoTask
import com.masum.todo.domain.model.Subtask
import java.util.Date

data class TodoUiState(
    val tasks: List<TodoTask> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val showTaskEditor: Boolean = false,
    val currentTaskHeading: String = "",
    val currentTaskBody: String = "",
    val currentTaskColor: TaskColor = TaskColor.DEFAULT,
    val taskToEdit: TodoTask? = null,
    val snackbarMessage: String? = null,
    val isGridView: Boolean = true
)

sealed class TodoUiEvent {
    data object LoadTasks : TodoUiEvent()
    data class AddTask(val heading: String, val body: String = "") : TodoUiEvent()
    data class AddAdvancedTask(
        val heading: String,
        val body: String,
        val color: TaskColor,
        val priority: TaskPriority,
        val dueDate: Date?,
        val subtasks: List<Subtask>,
        val tags: List<String>
    ) : TodoUiEvent()
    data class UpdateTask(val task: TodoTask) : TodoUiEvent()
    data class DeleteTask(val task: TodoTask) : TodoUiEvent()
    data class ToggleTaskCompletion(val task: TodoTask, val isCompleted: Boolean) : TodoUiEvent()
    data object ShowAddDialog : TodoUiEvent()
    data object HideAddDialog : TodoUiEvent()
    data object ShowTaskEditor : TodoUiEvent()
    data object HideTaskEditor : TodoUiEvent()
    data class ShowEditDialog(val task: TodoTask) : TodoUiEvent()
    data class ShowTaskEditorForEdit(val task: TodoTask) : TodoUiEvent()
    data object HideEditDialog : TodoUiEvent()
    data class UpdateCurrentTaskHeading(val heading: String) : TodoUiEvent()
    data class UpdateCurrentTaskBody(val body: String) : TodoUiEvent()
    data class UpdateCurrentTaskColor(val color: TaskColor) : TodoUiEvent()
    data object ClearSnackbarMessage : TodoUiEvent()
    data class ShowError(val message: String) : TodoUiEvent()
    data object ToggleViewMode : TodoUiEvent()
}
