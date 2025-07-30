package com.masum.todo.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.masum.todo.domain.model.TaskColor
import com.masum.todo.domain.model.TaskPriority
import com.masum.todo.domain.model.Subtask
import com.masum.todo.presentation.components.ColorPicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TaskEditorScreen(
    taskId: Int? = null,
    onNavigateBack: () -> Unit,
    onSaveTask: (
        heading: String,
        body: String,
        color: TaskColor,
        priority: TaskPriority,
        dueDate: Date?,
        subtasks: List<Subtask>,
        tags: List<String>
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var taskHeading by remember { mutableStateOf("") }
    var taskBody by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(TaskColor.DEFAULT) }
    var selectedPriority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var dueDate by remember { mutableStateOf<Date?>(null) }
    var subtasks by remember { mutableStateOf(listOf<Subtask>()) }
    var tags by remember { mutableStateOf(listOf<String>()) }
    var newSubtaskText by remember { mutableStateOf("") }
    var newTagText by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showPrioritySelector by remember { mutableStateOf(false) }
    var isBold by remember { mutableStateOf(false) }
    var isItalic by remember { mutableStateOf(false) }
    
    val keyboardController = LocalSoftwareKeyboardController.current
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (taskId == null) "Create Task" else "Edit Task",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (taskHeading.isNotBlank()) {
                        onSaveTask(
                            taskHeading,
                            taskBody,
                            selectedColor,
                            selectedPriority,
                            dueDate,
                            subtasks,
                            tags
                        )
                        onNavigateBack()
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Save Task"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save Task",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            
            OutlinedTextField(
                value = taskHeading,
                onValueChange = { taskHeading = it },
                label = { Text("Task Title") },
                placeholder = { Text("What needs to be done?") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Task,
                        contentDescription = "Task",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FormatButton(
                        icon = Icons.Default.FormatBold,
                        isSelected = isBold,
                        onClick = { isBold = !isBold }
                    )
                    FormatButton(
                        icon = Icons.Default.FormatItalic,
                        isSelected = isItalic,
                        onClick = { isItalic = !isItalic }
                    )
                    FormatButton(
                        icon = Icons.Default.FormatListBulleted,
                        isSelected = false,
                        onClick = { }
                    )
                    FormatButton(
                        icon = Icons.Default.FormatListNumbered,
                        isSelected = false,
                        onClick = { }
                    )
                    FormatButton(
                        icon = Icons.Default.Image,
                        isSelected = false,
                        onClick = { }
                    )
                    FormatButton(
                        icon = Icons.Default.KeyboardVoice,
                        isSelected = false,
                        onClick = { }
                    )
                    FormatButton(
                        icon = Icons.Default.Attachment,
                        isSelected = false,
                        onClick = { }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            
            OutlinedTextField(
                value = taskBody,
                onValueChange = { taskBody = it },
                label = { Text("Description") },
                placeholder = { Text("Add details about your task...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton(
                    icon = Icons.Default.Flag,
                    label = selectedPriority.displayName,
                    color = Color(selectedPriority.color),
                    onClick = { showPrioritySelector = !showPrioritySelector }
                )
                QuickActionButton(
                    icon = Icons.Default.CalendarToday,
                    label = dueDate?.let { dateFormat.format(it) } ?: "Due Date",
                    color = MaterialTheme.colorScheme.secondary,
                    onClick = { showDatePicker = true }
                )
                QuickActionButton(
                    icon = Icons.Default.Label,
                    label = "Color",
                    color = selectedColor.color,
                    onClick = { showColorPicker = !showColorPicker }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            
            AnimatedVisibility(
                visible = showPrioritySelector,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                PrioritySelector(
                    selectedPriority = selectedPriority,
                    onPrioritySelected = { 
                        selectedPriority = it
                        showPrioritySelector = false
                    }
                )
            }

            
            AnimatedVisibility(
                visible = showColorPicker,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    ColorPicker(
                        selectedColor = selectedColor,
                        onColorSelected = { 
                            selectedColor = it
                            showColorPicker = false
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            
            SubtasksSection(
                subtasks = subtasks,
                newSubtaskText = newSubtaskText,
                onNewSubtaskTextChange = { newSubtaskText = it },
                onAddSubtask = {
                    if (newSubtaskText.isNotBlank()) {
                        subtasks = subtasks + Subtask(
                            id = UUID.randomUUID().toString(),
                            title = newSubtaskText
                        )
                        newSubtaskText = ""
                    }
                },
                onToggleSubtask = { subtaskId ->
                    subtasks = subtasks.map { subtask ->
                        if (subtask.id == subtaskId) {
                            subtask.copy(isCompleted = !subtask.isCompleted)
                        } else subtask
                    }
                },
                onDeleteSubtask = { subtaskId ->
                    subtasks = subtasks.filter { it.id != subtaskId }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            
            TagsSection(
                tags = tags,
                newTagText = newTagText,
                onNewTagTextChange = { newTagText = it },
                onAddTag = {
                    if (newTagText.isNotBlank() && !tags.contains(newTagText)) {
                        tags = tags + newTagText
                        newTagText = ""
                    }
                },
                onRemoveTag = { tag ->
                    tags = tags.filter { it != tag }
                }
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            dueDate = Date(it)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun FormatButton(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(40.dp)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else Color.Transparent,
                CircleShape
            )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.primary 
                  else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PrioritySelector(
    selectedPriority: TaskPriority,
    onPrioritySelected: (TaskPriority) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Priority",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskPriority.values().forEach { priority ->
                    FilterChip(
                        selected = selectedPriority == priority,
                        onClick = { onPrioritySelected(priority) },
                        label = { Text(priority.displayName) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(
                                        Color(priority.color),
                                        CircleShape
                                    )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SubtasksSection(
    subtasks: List<Subtask>,
    newSubtaskText: String,
    onNewSubtaskTextChange: (String) -> Unit,
    onAddSubtask: () -> Unit,
    onToggleSubtask: (String) -> Unit,
    onDeleteSubtask: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Subtasks",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${subtasks.count { it.isCompleted }}/${subtasks.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newSubtaskText,
                    onValueChange = onNewSubtaskTextChange,
                    placeholder = { Text("Add subtask...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onAddSubtask() }),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = onAddSubtask,
                    modifier = Modifier.size(40.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Subtask",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            if (subtasks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                subtasks.forEach { subtask ->
                    SubtaskItem(
                        subtask = subtask,
                        onToggle = { onToggleSubtask(subtask.id) },
                        onDelete = { onDeleteSubtask(subtask.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun SubtaskItem(
    subtask: Subtask,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = subtask.isCompleted,
            onCheckedChange = { onToggle() }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = subtask.title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            color = if (subtask.isCompleted) 
                MaterialTheme.colorScheme.onSurfaceVariant 
            else 
                MaterialTheme.colorScheme.onSurface
        )
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Subtask",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagsSection(
    tags: List<String>,
    newTagText: String,
    onNewTagTextChange: (String) -> Unit,
    onAddTag: () -> Unit,
    onRemoveTag: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Tags",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newTagText,
                    onValueChange = onNewTagTextChange,
                    placeholder = { Text("Add tag...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onAddTag() }),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = onAddTag,
                    modifier = Modifier.size(40.dp),
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Tag",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            if (tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tags.forEach { tag ->
                        TagChip(
                            tag = tag,
                            onRemove = { onRemoveTag(tag) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TagChip(
    tag: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#$tag",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove Tag",
                modifier = Modifier
                    .size(16.dp)
                    .clickable { onRemove() },
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
