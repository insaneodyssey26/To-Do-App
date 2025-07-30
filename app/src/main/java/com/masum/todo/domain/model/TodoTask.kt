package com.masum.todo.domain.model

import java.util.Date

data class TodoTask(
    val id: Int = 0,
    val heading: String,
    val body: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val color: TaskColor = TaskColor.DEFAULT
)
