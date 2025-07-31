package com.masum.todo.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.masum.todo.presentation.viewmodel.FilterType
import com.masum.todo.presentation.viewmodel.SortType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchAndFilterBar(
    searchQuery: String,
    selectedFilter: FilterType,
    selectedSort: SortType,
    showSearchBar: Boolean,
    showFilterOptions: Boolean,
    filteredTasksCount: Int,
    onSearchQueryChange: (String) -> Unit,
    onFilterChange: (FilterType) -> Unit,
    onSortChange: (SortType) -> Unit,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        if (showSearchBar) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Search tasks, tags, or content...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = onClearSearch) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { keyboardController?.hide() }
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                if (searchQuery.isNotEmpty()) {
                    Text(
                        text = "$filteredTasksCount results found",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )
                }
                
                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickFilters = listOf(
                        FilterType.TODAY to "Today",
                        FilterType.OVERDUE to "Overdue",
                        FilterType.HIGH_PRIORITY to "High Priority",
                        FilterType.PENDING to "Active"
                    )
                    
                    quickFilters.forEach { (filter, label) ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { onFilterChange(filter) },
                            label = { Text(label, style = MaterialTheme.typography.bodySmall) }
                        )
                    }
                }
            }
        }
        
        if (showFilterOptions) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Smart Lists",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val smartFilters = listOf(
                            FilterType.ALL to "All Tasks",
                            FilterType.TODAY to "Due Today",
                            FilterType.OVERDUE to "Overdue",
                            FilterType.PENDING to "Active",
                            FilterType.COMPLETED to "Completed",
                            FilterType.HIGH_PRIORITY to "High Priority",
                            FilterType.WITH_DUE_DATE to "With Due Date",
                            FilterType.NO_DUE_DATE to "No Due Date"
                        )
                        
                        smartFilters.forEach { (filter, label) ->
                            FilterChip(
                                selected = selectedFilter == filter,
                                onClick = { onFilterChange(filter) },
                                label = { Text(label) },
                                leadingIcon = {
                                    val color = when (filter) {
                                        FilterType.OVERDUE -> Color.Red
                                        FilterType.TODAY -> Color(0xFFFF9800)
                                        FilterType.HIGH_PRIORITY -> Color(0xFFE91E63)
                                        FilterType.COMPLETED -> Color(0xFF4CAF50)
                                        FilterType.PENDING -> Color(0xFF2196F3)
                                        else -> MaterialTheme.colorScheme.primary
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(color, CircleShape)
                                    )
                                }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Sort By",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val sortOptions = listOf(
                            SortType.CREATED_DATE to "Created Date",
                            SortType.DUE_DATE to "Due Date",
                            SortType.PRIORITY to "Priority",
                            SortType.ALPHABETICAL to "A-Z",
                            SortType.COMPLETION_STATUS to "Status"
                        )
                        
                        sortOptions.forEach { (sort, label) ->
                            FilterChip(
                                selected = selectedSort == sort,
                                onClick = { onSortChange(sort) },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }
        }
    }
}
