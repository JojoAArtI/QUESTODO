package com.example.quest.data.repository

import com.example.quest.data.local.dao.TaskDao
import com.example.quest.data.local.entity.TaskEntity
import com.example.quest.data.local.entity.TaskPriority
import com.example.quest.data.local.entity.TaskStatus
import com.example.quest.data.local.entity.RecurringType
import com.example.quest.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class TaskRepository(private val taskDao: TaskDao) {

    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks().map { entities ->
        entities.map { it.toTask() }
    }

    fun getTasksByStatus(status: TaskStatus): Flow<List<Task>> = 
        taskDao.getTasksByStatus(status.ordinal).map { entities ->
            entities.map { it.toTask() }
        }

    suspend fun getTaskById(taskId: Long): Task? = taskDao.getTaskById(taskId)?.toTask()

    suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task.toEntity())
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }
    
    suspend fun deleteTaskById(taskId: Long) {
        taskDao.deleteTaskById(taskId)
    }

    suspend fun completeTask(taskId: Long) {
        // Mark  done and  will disappear
        taskDao.updateTaskStatus(taskId, TaskStatus.DONE.ordinal, true, System.currentTimeMillis())
    }

    // Mappers
    private fun TaskEntity.toTask(): Task = Task(
        id = id,
        title = title,
        description = description,
        notes = notes,
        priority = TaskPriority.values().getOrElse(priority) { TaskPriority.MEDIUM },
        status = TaskStatus.values().getOrElse(status) { TaskStatus.TODO },
        categoryId = categoryId,
        dueDate = dueDate?.let { 
            LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
        },
        reminderTime = reminderTime?.let {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
        },
        recurringType = RecurringType.values().getOrElse(recurringType) { RecurringType.NONE },
        isCompleted = isCompleted,
        completedAt = completedAt?.let {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
        },
        createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault())
    )

    private fun Task.toEntity(): TaskEntity = TaskEntity(
        id = id,
        title = title,
        description = description,
        notes = notes,
        priority = priority.ordinal,
        status = status.ordinal,
        categoryId = categoryId,
        dueDate = dueDate?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
        reminderTime = reminderTime?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
        recurringType = recurringType.ordinal,
        isCompleted = isCompleted,
        completedAt = completedAt?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
        createdAt = createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
}
