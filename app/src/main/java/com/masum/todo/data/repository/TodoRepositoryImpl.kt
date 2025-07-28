package com.masum.todo.data.repository

import com.masum.todo.data.database.TodoTaskDao
import com.masum.todo.data.database.toDomainModel
import com.masum.todo.data.database.toEntity
import com.masum.todo.domain.model.TodoTask
import com.masum.todo.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class TodoRepositoryImpl(
    private val dao: TodoTaskDao
) : TodoRepository {
    
    override fun getAllTasks(): Flow<List<TodoTask>> {
        return dao.getAllTasks().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
    
    override suspend fun getTaskById(id: Int): TodoTask? {
        return dao.getTaskById(id)?.toDomainModel()
    }
    
    override suspend fun insertTask(task: TodoTask): Long {
        val taskWithTimestamp = task.copy(
            createdAt = Date(),
            updatedAt = Date()
        )
        return dao.insertTask(taskWithTimestamp.toEntity())
    }
    
    override suspend fun updateTask(task: TodoTask) {
        val taskWithTimestamp = task.copy(updatedAt = Date())
        dao.updateTask(taskWithTimestamp.toEntity())
    }
    
    override suspend fun deleteTask(task: TodoTask) {
        dao.deleteTask(task.toEntity())
    }
    
    override suspend fun deleteTaskById(id: Int) {
        dao.deleteTaskById(id)
    }
    
    override suspend fun getTaskCount(): Int {
        return dao.getTaskCount()
    }
    
    override suspend fun getCompletedTaskCount(): Int {
        return dao.getCompletedTaskCount()
    }
}
