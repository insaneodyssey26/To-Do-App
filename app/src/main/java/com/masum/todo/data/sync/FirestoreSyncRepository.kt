package com.masum.todo.data.sync

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.masum.todo.domain.model.TodoTask
import com.masum.todo.domain.model.TaskColor
import com.masum.todo.domain.model.TaskPriority
import kotlinx.coroutines.tasks.await
import java.util.*

class FirestoreSyncRepository {
    private val firestore = FirebaseFirestore.getInstance()
    
    suspend fun syncTaskToCloud(userId: String, task: TodoTask) {
        val taskData = mapOf(
            "id" to task.id,
            "heading" to task.heading,
            "body" to task.body,
            "isCompleted" to task.isCompleted,
            "createdAt" to task.createdAt,
            "dueDate" to task.dueDate,
            "priority" to task.priority.name,
            "color" to task.color.name,
            "tags" to task.tags,
            "lastModified" to Date()
        )
        
        firestore.collection("users")
            .document(userId)
            .collection("tasks")
            .document(task.id.toString())
            .set(taskData)
            .await()
    }
    
    suspend fun deleteTaskFromCloud(userId: String, taskId: Long) {
        firestore.collection("users")
            .document(userId)
            .collection("tasks")
            .document(taskId.toString())
            .delete()
            .await()
    }
    
    suspend fun getTasksFromCloud(userId: String): List<TodoTask> {
        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("tasks")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
        
        return snapshot.documents.mapNotNull { doc ->
            try {
                TodoTask(
                    id = doc.getString("id")?.toLongOrNull() ?: 0L,
                    heading = doc.getString("heading") ?: "",
                    body = doc.getString("body") ?: "",
                    isCompleted = doc.getBoolean("isCompleted") ?: false,
                    createdAt = doc.getDate("createdAt") ?: Date(),
                    dueDate = doc.getDate("dueDate"),
                    priority = TaskPriority.valueOf(doc.getString("priority") ?: "MEDIUM"),
                    color = TaskColor.valueOf(doc.getString("color") ?: "DEFAULT"),
                    tags = doc.get("tags") as? List<String> ?: emptyList()
                )
            } catch (e: Exception) {
                null
            }
        }
    }
    
    suspend fun syncAllTasksToCloud(userId: String, tasks: List<TodoTask>) {
        val batch = firestore.batch()
        val userTasksRef = firestore.collection("users").document(userId).collection("tasks")
        
        tasks.forEach { task ->
            val taskData = mapOf(
                "id" to task.id,
                "heading" to task.heading,
                "body" to task.body,
                "isCompleted" to task.isCompleted,
                "createdAt" to task.createdAt,
                "dueDate" to task.dueDate,
                "priority" to task.priority.name,
                "color" to task.color.name,
                "tags" to task.tags,
                "lastModified" to Date()
            )
            
            batch.set(userTasksRef.document(task.id.toString()), taskData)
        }
        
        batch.commit().await()
    }
}
