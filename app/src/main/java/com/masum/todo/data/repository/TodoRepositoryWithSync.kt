package com.masum.todo.data.repository

import com.masum.todo.data.auth.AuthService
import com.masum.todo.data.sync.FirestoreSyncRepository
import com.masum.todo.domain.model.TodoTask
import com.masum.todo.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow

class TodoRepositoryWithSync(
    private val localRepository: TodoRepositoryImpl,
    private val syncRepository: FirestoreSyncRepository,
    private val authService: AuthService
) : TodoRepository {
    
    override fun getAllTasks(): Flow<List<TodoTask>> {
        return localRepository.getAllTasks()
    }
    
    override suspend fun getTaskById(id: Int): TodoTask? {
        return localRepository.getTaskById(id)
    }
    
    override suspend fun insertTask(task: TodoTask): Long {
        val taskId = localRepository.insertTask(task).toLong()
        
        if (authService.isUserLoggedIn()) {
            try {
                val userId = authService.getUserId()!!
                val taskWithId = task.copy(id = taskId.toInt().toLong())
                syncRepository.syncTaskToCloud(userId, taskWithId)
            } catch (e: Exception) {
            }
        }
        
        return taskId
    }
    
    override suspend fun updateTask(task: TodoTask) {
        localRepository.updateTask(task)
        
        if (authService.isUserLoggedIn()) {
            try {
                val userId = authService.getUserId()!!
                syncRepository.syncTaskToCloud(userId, task)
            } catch (e: Exception) {
            }
        }
    }
    
    override suspend fun deleteTask(task: TodoTask) {
        localRepository.deleteTask(task)
        
        if (authService.isUserLoggedIn()) {
            try {
                val userId = authService.getUserId()!!
                syncRepository.deleteTaskFromCloud(userId, task.id.toLong())
            } catch (e: Exception) {
            }
        }
    }
    
    override suspend fun deleteTaskById(id: Int) {
        localRepository.deleteTaskById(id)
        
        if (authService.isUserLoggedIn()) {
            try {
                val userId = authService.getUserId()!!
                syncRepository.deleteTaskFromCloud(userId, id.toLong())
            } catch (e: Exception) {
            }
        }
    }
    
    override suspend fun getTaskCount(): Int {
        return localRepository.getTaskCount()
    }
    
    override suspend fun getCompletedTaskCount(): Int {
        return localRepository.getCompletedTaskCount()
    }
    
    suspend fun syncFromCloud(): Result<List<TodoTask>> {
        return try {
            if (!authService.isUserLoggedIn()) {
                return Result.failure(Exception("User not logged in"))
            }
            
            val userId = authService.getUserId()!!
            val cloudTasks = syncRepository.getTasksFromCloud(userId)
            
            // Merge with local tasks (you might want more sophisticated merging logic)
            cloudTasks.forEach { cloudTask ->
                try {
                    localRepository.insertTask(cloudTask)
                } catch (e: Exception) {
                    // Task might already exist, try updating
                    try {
                        localRepository.updateTask(cloudTask)
                    } catch (e: Exception) {
                        // Log error
                    }
                }
            }
            
            Result.success(cloudTasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun syncToCloud(tasks: List<TodoTask>): Result<Unit> {
        return try {
            if (!authService.isUserLoggedIn()) {
                return Result.failure(Exception("User not logged in"))
            }
            
            val userId = authService.getUserId()!!
            syncRepository.syncAllTasksToCloud(userId, tasks)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
