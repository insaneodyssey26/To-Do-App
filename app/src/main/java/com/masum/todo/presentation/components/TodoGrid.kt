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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.masum.todo.domain.model.TodoTask

@Composable
fun TodoGrid(
    tasks: List<TodoTask>,
    onTaskChecked: (TodoTask, Boolean) -> Unit,
    onDeleteTask: (TodoTask) -> Unit,
    onEditTask: (TodoTask) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
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
                TodoGridCard(
                    task = task,
                    onCheckedChange = { isChecked -> onTaskChecked(task, isChecked) },
                    onDelete = { onDeleteTask(task) },
                    onEdit = { onEditTask(task) }
                )
            }
        }
    }
}
