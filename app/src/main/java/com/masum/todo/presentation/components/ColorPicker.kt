package com.masum.todo.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.masum.todo.domain.model.TaskColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPicker(
    selectedColor: TaskColor,
    onColorSelected: (TaskColor) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        maxItemsInEachRow = 6
    ) {
        TaskColor.values().forEach { color ->
            ColorItem(
                color = color,
                isSelected = selectedColor == color,
                onColorSelected = { onColorSelected(color) }
            )
        }
    }
}

@Composable
private fun ColorItem(
    color: TaskColor,
    isSelected: Boolean,
    onColorSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(200),
        label = "border_color_animation"
    )
    
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(
                if (color == TaskColor.TRANSPARENT) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    color.color
                }
            )
            .border(
                width = 2.dp,
                color = animatedBorderColor,
                shape = CircleShape
            )
            .clickable { onColorSelected() },
        contentAlignment = Alignment.Center
    ) {
        if (color == TaskColor.TRANSPARENT) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "No Color",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        } else if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = if (color == TaskColor.DEFAULT || color == TaskColor.YELLOW) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.White
                },
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
