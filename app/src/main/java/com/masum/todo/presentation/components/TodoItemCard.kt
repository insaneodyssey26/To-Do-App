package com.masum.todo.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.masum.todo.domain.model.TodoTask
import com.masum.todo.domain.model.TaskColor
import com.masum.todo.ui.theme.TaskCardBackground
import com.masum.todo.ui.theme.TaskCardBorder
import com.masum.todo.ui.theme.CompletedTaskOverlay
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TodoItemCard(
    task: TodoTask,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(task.createdAt)
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                task.color == TaskColor.TRANSPARENT -> {
                    if (task.isCompleted) 
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    else 
                        MaterialTheme.colorScheme.surface
                }
                task.isCompleted -> task.color.color.copy(alpha = 0.2f)
                else -> task.color.color.copy(alpha = 0.8f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (task.isCompleted) 2.dp else 8.dp
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box {
            if (task.color != TaskColor.TRANSPARENT && !task.isCompleted) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    task.color.color.copy(alpha = 0.6f),
                                    task.color.color.copy(alpha = 0.3f)
                                )
                            )
                        )
                )
            }
            
            if (task.isCompleted) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            CompletedTaskOverlay,
                            RoundedCornerShape(20.dp)
                        )
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .alpha(if (task.isCompleted) 0.7f else 1f)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = onCheckedChange,
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.outline,
                            checkmarkColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = task.heading,
                            style = MaterialTheme.typography.titleMedium.copy(
                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = if (task.isCompleted) 
                                MaterialTheme.colorScheme.onSurfaceVariant 
                            else 
                                MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        if (task.body.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = task.body,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                                ),
                                color = if (task.isCompleted) 
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                            
                            Row {
                                IconButton(
                                    onClick = onEdit,
                                    colors = IconButtonDefaults.iconButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Task",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                
                                IconButton(
                                    onClick = onDelete,
                                    colors = IconButtonDefaults.iconButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    ),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Task",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
