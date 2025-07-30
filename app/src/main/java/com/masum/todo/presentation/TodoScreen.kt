package com.masum.todo.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.width
import androidx.compose.ui.draw.scale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.masum.todo.presentation.components.EmptyState
import com.masum.todo.presentation.components.SearchAndFilterBar
import com.masum.todo.presentation.components.TaskDialog
import com.masum.todo.presentation.components.TodoGrid
import com.masum.todo.presentation.components.TodoList
import com.masum.todo.presentation.components.ViewToggleButton
import com.masum.todo.presentation.viewmodel.TodoUiEvent
import com.masum.todo.presentation.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    viewModel: TodoViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onEvent(TodoUiEvent.ClearSnackbarMessage)
        }
    }

    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .statusBarsPadding(),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "My Tasks",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (uiState.tasks.isNotEmpty()) {
                            val completedCount = uiState.tasks.count { it.isCompleted }
                            val totalCount = uiState.tasks.size
                            Text(
                                text = "$completedCount of $totalCount completed",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                actions = {
                    Row {
                        IconButton(
                            onClick = {
                                viewModel.onEvent(TodoUiEvent.ToggleSearchBar)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = if (uiState.showSearchBar) MaterialTheme.colorScheme.primary
                                       else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        IconButton(
                            onClick = {
                                viewModel.onEvent(TodoUiEvent.ToggleFilterOptions)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = if (uiState.showFilterOptions) MaterialTheme.colorScheme.primary
                                       else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        ViewToggleButton(
                            isGridView = uiState.isGridView,
                            onToggleView = {
                                viewModel.onEvent(TodoUiEvent.ToggleViewMode)
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            val fabScale by animateFloatAsState(
                targetValue = if (uiState.isLoading) 0.8f else 1f,
                animationSpec = tween(300),
                label = "fab_scale"
            )
            
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.onEvent(TodoUiEvent.ShowTaskEditor)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.scale(fabScale)
            ) {
                Icon(
                    Icons.Default.Add, 
                    contentDescription = "Create Task",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Create Task",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                val tasksToShow = if (uiState.searchQuery.isNotEmpty() ||
                                    uiState.selectedFilter != com.masum.todo.presentation.viewmodel.FilterType.ALL) {
                    uiState.filteredTasks
                } else {
                    uiState.tasks
                }
                SearchAndFilterBar(
                    searchQuery = uiState.searchQuery,
                    selectedFilter = uiState.selectedFilter,
                    selectedSort = uiState.selectedSort,
                    showSearchBar = uiState.showSearchBar,
                    showFilterOptions = uiState.showFilterOptions,
                    filteredTasksCount = tasksToShow.size,
                    onSearchQueryChange = { query ->
                        viewModel.onEvent(TodoUiEvent.UpdateSearchQuery(query))
                    },
                    onFilterChange = { filter ->
                        viewModel.onEvent(TodoUiEvent.UpdateFilter(filter))
                    },
                    onSortChange = { sort ->
                        viewModel.onEvent(TodoUiEvent.UpdateSort(sort))
                    },
                    onClearSearch = {
                        viewModel.onEvent(TodoUiEvent.ClearSearch)
                    }
                )
                
                if (uiState.tasks.isNotEmpty()) {
                    TaskSummaryCard(
                        totalTasks = uiState.tasks.size,
                        completedTasks = uiState.tasks.count { it.isCompleted },
                        filteredCount = if (tasksToShow != uiState.tasks) tasksToShow.size else null,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 3.dp
                            )
                        }
                    }
                    uiState.tasks.isEmpty() -> {
                        EmptyState()
                    }
                    tasksToShow.isEmpty() -> {
                        EmptySearchState()
                    }
                    else -> {
                        if (uiState.isGridView) {
                            TodoGrid(
                                tasks = tasksToShow,
                                onTaskChecked = { task, isChecked ->
                                    viewModel.onEvent(TodoUiEvent.ToggleTaskCompletion(task, isChecked))
                                },
                                onDeleteTask = { task ->
                                    viewModel.onEvent(TodoUiEvent.DeleteTask(task))
                                },
                                onEditTask = { task ->
                                    viewModel.onEvent(TodoUiEvent.ShowTaskEditorForEdit(task))
                                }
                            )
                        } else {
                            TodoList(
                                tasks = tasksToShow,
                                onTaskChecked = { task, isChecked ->
                                    viewModel.onEvent(TodoUiEvent.ToggleTaskCompletion(task, isChecked))
                                },
                                onDeleteTask = { task ->
                                    viewModel.onEvent(TodoUiEvent.DeleteTask(task))
                                },
                                onEditTask = { task ->
                                    viewModel.onEvent(TodoUiEvent.ShowTaskEditorForEdit(task))
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.showAddDialog) {
        TaskDialog(
            dialogTitle = "Add New Task",
            taskHeading = uiState.currentTaskHeading,
            taskBody = uiState.currentTaskBody,
            taskColor = uiState.currentTaskColor,
            onHeadingChange = { heading ->
                viewModel.onEvent(TodoUiEvent.UpdateCurrentTaskHeading(heading))
            },
            onBodyChange = { body ->
                viewModel.onEvent(TodoUiEvent.UpdateCurrentTaskBody(body))
            },
            onColorChange = { color ->
                viewModel.onEvent(TodoUiEvent.UpdateCurrentTaskColor(color))
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
            taskColor = uiState.currentTaskColor,
            onHeadingChange = { heading ->
                viewModel.onEvent(TodoUiEvent.UpdateCurrentTaskHeading(heading))
            },
            onBodyChange = { body ->
                viewModel.onEvent(TodoUiEvent.UpdateCurrentTaskBody(body))
            },
            onColorChange = { color ->
                viewModel.onEvent(TodoUiEvent.UpdateCurrentTaskColor(color))
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

    
    if (uiState.showTaskEditor) {
        val taskToEdit = uiState.taskToEdit
        TaskEditorScreen(
            taskId = taskToEdit?.id,
            existingTask = taskToEdit,
            onNavigateBack = {
                viewModel.onEvent(TodoUiEvent.HideTaskEditor)
            },
            onSaveTask = { heading, body, color, priority, dueDate, subtasks, tags ->
                if (taskToEdit != null) {
                    val updatedTask = taskToEdit.copy(
                        heading = heading,
                        body = body,
                        color = color,
                        priority = priority,
                        dueDate = dueDate,
                        subtasks = subtasks,
                        tags = tags
                    )
                    viewModel.onEvent(TodoUiEvent.UpdateTask(updatedTask))
                } else {
                    viewModel.onEvent(
                        TodoUiEvent.AddAdvancedTask(
                            heading = heading,
                            body = body,
                            color = color,
                            priority = priority,
                            dueDate = dueDate,
                            subtasks = subtasks,
                            tags = tags
                        )
                    )
                }
            }
        )
    }
}

@Composable
private fun EmptySearchState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No tasks found",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Try adjusting your search or filters",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TaskSummaryCard(
    totalTasks: Int,
    completedTasks: Int,
    filteredCount: Int? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TaskStatItem(
                icon = Icons.Default.List,
                label = "Total",
                count = totalTasks,
                color = MaterialTheme.colorScheme.primary
            )
            
            TaskStatItem(
                icon = Icons.Default.CheckCircle,
                label = "Completed",
                count = completedTasks,
                color = Color(0xFF4CAF50)
            )
            
            if (filteredCount != null) {
                TaskStatItem(
                    icon = Icons.Default.FilterList,
                    label = "Filtered",
                    count = filteredCount,
                    color = MaterialTheme.colorScheme.secondary
                )
            } else {
                TaskStatItem(
                    icon = Icons.Default.List,
                    label = "Remaining",
                    count = totalTasks - completedTasks,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
private fun TaskStatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.1f))
                .padding(6.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
