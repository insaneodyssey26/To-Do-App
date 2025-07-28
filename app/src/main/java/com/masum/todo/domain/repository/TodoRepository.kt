package com.masum.todo.domain.repository

import com.masum.todo.domain.model.TodoTask
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getAllTasks(): Flow<List<TodoTask>>
    suspend fun getTaskById(id: Int): TodoTask?
    suspend fun insertTask(task: TodoTask): Long
    suspend fun updateTask(task: TodoTask)
    suspend fun deleteTask(task: TodoTask)
    suspend fun deleteTaskById(id: Int)
    suspend fun getTaskCount(): Int
    suspend fun getCompletedTaskCount(): Int
}
