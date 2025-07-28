package com.masum.todo.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masum.todo.presentation.components.EmptyState
import com.masum.todo.presentation.components.TaskDialog
import com.masum.todo.presentation.components.TodoList
import com.masum.todo.presentation.viewmodel.TodoUiEvent
import com.masum.todo.presentation.viewmodel.TodoViewModel

@Composable
fun TodoScreen(
    viewModel: TodoViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }


    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(TodoUiEvent.ClearSnackbarMessage)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(TodoUiEvent.ShowAddDialog)
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                uiState.tasks.isEmpty() -> {
                    EmptyState()
                }
                else -> {
                    TodoList(
                        tasks = uiState.tasks,
                        onTaskChecked = { task, isChecked ->
                            viewModel.onEvent(TodoUiEvent.ToggleTaskCompletion(task, isChecked))
                        },
                        onDeleteTask = { task ->
                            viewModel.onEvent(TodoUiEvent.DeleteTask(task))
                        },
                        onEditTask = { task ->
                            viewModel.onEvent(TodoUiEvent.ShowEditDialog(task))
                        }
                    )
                }
            }
        }
    }


    if (uiState.showAddDialog) {
        TaskDialog(
            dialogTitle = "Add New Task",
            taskHeading = uiState.currentTaskHeading,
            taskBody = uiState.currentTaskBody,
            onHeadingChange = { heading ->
                viewModel.onEvent(TodoUiEvent.UpdateCurrentTaskHeading(heading))
            },
            onBodyChange = { body ->
                viewModel.onEvent(TodoUiEvent.UpdateCurrentTaskBody(body))
            },
            onDismiss = {
                viewModel.onEvent(TodoUiEvent.HideAddDialog)
            },
            onConfirm = {
                viewModel.onEvent(TodoUiEvent.AddTask(uiState.currentTaskHeading, uiState.currentTaskBody))
            },
            confirmButtonText = "Add"
        )
    }


    if (uiState.showEditDialog && uiState.taskToEdit != null) {
        TaskDialog(
            dialogTitle = "Edit Task",
            taskHeading = uiState.currentTaskHeading,
            taskBody = uiState.currentTaskBody,
            onHeadingChange = { heading ->
                viewModel.onEvent(TodoUiEvent.UpdateCurrentTaskHeading(heading))
            },
            onBodyChange = { body ->
                viewModel.onEvent(TodoUiEvent.UpdateCurrentTaskBody(body))
            },
            onDismiss = {
                viewModel.onEvent(TodoUiEvent.HideEditDialog)
            },
            onConfirm = {
                uiState.taskToEdit?.let { task ->
                    viewModel.onEvent(TodoUiEvent.UpdateTask(task))
                }
            },
            confirmButtonText = "Save"
        )
    }
}
