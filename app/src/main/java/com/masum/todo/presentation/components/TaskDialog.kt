package com.masum.todo.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.masum.todo.domain.model.TaskColor

@Composable
fun TaskDialog(
    dialogTitle: String,
    taskHeading: String,
    taskBody: String,
    taskColor: TaskColor,
    onHeadingChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onColorChange: (TaskColor) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmButtonText: String,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = dialogTitle,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = taskHeading,
                    onValueChange = onHeadingChange,
                    label = { Text("Task heading") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Task,
                            contentDescription = "Task heading",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = taskBody,
                    onValueChange = onBodyChange,
                    label = { Text("Task description (optional)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Task description",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        focusedLabelColor = MaterialTheme.colorScheme.secondary,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Choose Color",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                ColorPicker(
                    selectedColor = taskColor,
                    onColorSelected = onColorChange
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = taskHeading.isNotBlank()
            ) {
                Text(
                    text = confirmButtonText,
                    fontWeight = FontWeight.SemiBold,
                    color = if (taskHeading.isNotBlank()) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
