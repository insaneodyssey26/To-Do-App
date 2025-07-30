package com.masum.todo.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

@Composable
fun ViewToggleButton(
    isGridView: Boolean,
    onToggleView: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isGridView) 0f else 180f,
        animationSpec = tween(300),
        label = "view_toggle_rotation"
    )
    
    IconButton(
        onClick = onToggleView,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isGridView) Icons.Filled.ViewList else Icons.Filled.ViewModule,
            contentDescription = if (isGridView) "Switch to list view" else "Switch to grid view",
            modifier = Modifier
                .size(24.dp)
                .rotate(rotation)
        )
    }
}
