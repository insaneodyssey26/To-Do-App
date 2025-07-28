package com.masum.todo.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.masum.todo.domain.model.TodoTask
import java.util.Date

@Entity(tableName = "todo_tasks")
@TypeConverters(DateConverter::class)
data class TodoTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val heading: String,
    val body: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)


fun TodoTaskEntity.toDomainModel(): TodoTask {
    return TodoTask(
        id = id,
        heading = heading,
        body = body,
        isCompleted = isCompleted,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun TodoTask.toEntity(): TodoTaskEntity {
    return TodoTaskEntity(
        id = id,
        heading = heading,
        body = body,
        isCompleted = isCompleted,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
