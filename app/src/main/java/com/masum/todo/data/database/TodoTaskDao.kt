package com.masum.todo.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoTaskDao {
    
    @Query("SELECT * FROM todo_tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TodoTaskEntity>>
    
    @Query("SELECT * FROM todo_tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): TodoTaskEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TodoTaskEntity): Long
    
    @Update
    suspend fun updateTask(task: TodoTaskEntity)
    
    @Delete
    suspend fun deleteTask(task: TodoTaskEntity)
    
    @Query("DELETE FROM todo_tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Int)
    
    @Query("SELECT COUNT(*) FROM todo_tasks")
    suspend fun getTaskCount(): Int
    
    @Query("SELECT COUNT(*) FROM todo_tasks WHERE isCompleted = 1")
    suspend fun getCompletedTaskCount(): Int
}
