package com.masum.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.masum.todo.ui.theme.TodoTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoTheme {
                TodoAppScreen()
            }
        }
    }
}

// Updated data class to include heading and body
data class TodoTask(
    val id: Int,
    val heading: String,
    val body: String = "",
    var isCompleted: Boolean = false,
    val createdAt: Date = Date()
)

@Composable
fun TodoAppScreen() {
    var todos by remember { mutableStateOf(emptyList<TodoTask>()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var currentTaskHeading by remember { mutableStateOf("") }
    var currentTaskBody by remember { mutableStateOf("") }
    var taskToEdit by remember { mutableStateOf<TodoTask?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    var actionMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(actionMessage) {
        actionMessage?.let {
            snackbarHostState.showSnackbar(it)
            actionMessage = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddDialog = true
                    currentTaskHeading = ""
                    currentTaskBody = ""
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (todos.isEmpty()) {
                EmptyStateAnimation()
            } else {
                TodoListWithAnimations(
                    todos = todos,
                    onTaskChecked = { task, isChecked ->
                        todos = todos.map {
                            if (it.id == task.id) it.copy(isCompleted = isChecked)
                            else it
                        }
                        val status = if (isChecked) "completed" else "marked active"
                        actionMessage = "Task ${status}: ${task.heading}"
                    },
                    onDeleteTask = { task ->
                        todos = todos.filter { it.id != task.id }
                        actionMessage = "Task deleted: ${task.heading}"
                    },
                    onEditTask = { task ->
                        taskToEdit = task
                        currentTaskHeading = task.heading
                        currentTaskBody = task.body
                        showEditDialog = true
                    }
                )
            }
        }
    }

    if (showAddDialog) {
        TaskDialogWithHeadingAndBody(
            dialogTitle = "Add New Task",
            taskHeading = currentTaskHeading,
            taskBody = currentTaskBody,
            onHeadingChange = { currentTaskHeading = it },
            onBodyChange = { currentTaskBody = it },
            onDismiss = {
                showAddDialog = false
                currentTaskHeading = ""
                currentTaskBody = ""
            },
            onConfirm = {
                if (currentTaskHeading.isNotBlank()) {
                    val newTask = TodoTask(
                        id = if (todos.isEmpty()) 1 else todos.maxOf { it.id } + 1,
                        heading = currentTaskHeading,
                        body = currentTaskBody,
                        createdAt = Date()
                    )
                    todos = todos + newTask
                    currentTaskHeading = ""
                    currentTaskBody = ""
                    showAddDialog = false
                    actionMessage = "Task added: ${newTask.heading}"
                }
            },
            confirmButtonText = "Add"
        )
    }

    if (showEditDialog && taskToEdit != null) {
        TaskDialogWithHeadingAndBody(
            dialogTitle = "Edit Task",
            taskHeading = currentTaskHeading,
            taskBody = currentTaskBody,
            onHeadingChange = { currentTaskHeading = it },
            onBodyChange = { currentTaskBody = it },
            onDismiss = {
                showEditDialog = false
                taskToEdit = null
            },
            onConfirm = {
                if (currentTaskHeading.isNotBlank()) {
                    taskToEdit?.let { task ->
                        todos = todos.map {
                            if (it.id == task.id) it.copy(heading = currentTaskHeading, body = currentTaskBody)
                            else it
                        }
                        actionMessage = "Task updated: $currentTaskHeading"
                    }
                    showEditDialog = false
                    taskToEdit = null
                }
            },
            confirmButtonText = "Save"
        )
    }
}

@Composable
fun TodoListWithAnimations(
    todos: List<TodoTask>,
    onTaskChecked: (TodoTask, Boolean) -> Unit,
    onDeleteTask: (TodoTask) -> Unit,
    onEditTask: (TodoTask) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            items = todos,
            key = { it.id }
        ) { task ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                TodoItemCard(
                    task = task,
                    onCheckedChange = { isChecked -> onTaskChecked(task, isChecked) },
                    onDelete = { onDeleteTask(task) },
                    onEdit = { onEditTask(task) }
                )
            }
        }
    }
}

@Composable
fun TodoItemCard(
    task: TodoTask,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(task.createdAt)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = onCheckedChange
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    // Task heading
                    Text(
                        text = task.heading,
                        style = MaterialTheme.typography.titleMedium.copy(
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    // Only show body if it's not empty
                    if (task.body.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.body,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                            )
                        )
                    }
                }
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Task",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                TextButton(onClick = onDelete) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Created: $formattedDate",
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 48.dp)
            )
        }
    }
}

@Composable
fun EmptyStateAnimation() {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.CenterVertically),
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No tasks yet!\nTap + to add a new task",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// New dialog that handles both heading and body
@Composable
fun TaskDialogWithHeadingAndBody(
    dialogTitle: String,
    taskHeading: String,
    taskBody: String,
    onHeadingChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmButtonText: String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dialogTitle) },
        text = {
            Column {
                // Task Heading field
                OutlinedTextField(
                    value = taskHeading,
                    onValueChange = onHeadingChange,
                    label = { Text("Task heading") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Task Body field
                OutlinedTextField(
                    value = taskBody,
                    onValueChange = onBodyChange,
                    label = { Text("Task description (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun TodoAppPreview() {
    TodoTheme {
        TodoAppScreen()
    }
}