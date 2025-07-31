package com.masum.todo.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.masum.todo.domain.model.TaskColor
import com.masum.todo.domain.model.TodoTask
import java.util.Date

@Entity(tableName = "todo_tasks")
@TypeConverters(DateConverter::class, TaskColorConverter::class)
data class TodoTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val heading: String,
    val body: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val color: TaskColor = TaskColor.DEFAULT
)


fun TodoTaskEntity.toDomainModel(): TodoTask {
    return TodoTask(
        id = id.toLong(),
        heading = heading,
        body = body,
        isCompleted = isCompleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
        color = color
    )
}

fun TodoTask.toEntity(): TodoTaskEntity {
    return TodoTaskEntity(
        id = id,
        heading = heading,
        body = body,
        isCompleted = isCompleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
        color = color
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

class TaskColorConverter {
    @TypeConverter
    fun fromTaskColor(color: TaskColor): Int {
        return color.ordinal
    }

    @TypeConverter
    fun toTaskColor(ordinal: Int): TaskColor {
        return TaskColor.fromOrdinal(ordinal)
    }
}
