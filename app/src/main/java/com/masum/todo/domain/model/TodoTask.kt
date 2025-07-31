package com.masum.todo.domain.model

import java.util.Date

data class TodoTask(
    val id: Long = 0,
    val heading: String,
    val body: String = "",
    val richTextBody: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val color: TaskColor = TaskColor.DEFAULT,
    val attachments: List<TaskAttachment> = emptyList(),
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueDate: Date? = null,
    val tags: List<String> = emptyList()
)

data class TaskAttachment(
    val id: String,
    val fileName: String,
    val filePath: String,
    val fileType: AttachmentType,
    val fileSize: Long,
    val createdAt: Date = Date()
)

enum class AttachmentType {
    IMAGE,
    DOCUMENT,
    AUDIO,
    OTHER
}

enum class TaskPriority(val displayName: String, val color: Long) {
    LOW("Low", 0xFF4CAF50),
    MEDIUM("Medium", 0xFFFF9800),
    HIGH("High", 0xFFF44336),
    URGENT("Urgent", 0xFF9C27B0)
}
