package com.masum.todo.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.masum.todo.domain.model.TodoTask

@Composable
fun TodoList(
    tasks: List<TodoTask>,
    onTaskChecked: (TodoTask, Boolean) -> Unit,
    onDeleteTask: (TodoTask) -> Unit,
    onEditTask: (TodoTask) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 4.dp)
    ) {
        items(
            items = tasks,
            key = { it.id }
        ) { task ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(300)) + 
                        expandVertically(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(200)) + 
                       shrinkVertically(animationSpec = tween(200))
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
