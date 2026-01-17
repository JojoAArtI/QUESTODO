package com.example.quest.data.local.dao

import androidx.room.*
import com.example.quest.data.local.entity.TaskEntity
import com.example.quest.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    // Get all tasks including completed ones - sorted by completion, then status, priority
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, status ASC, priority ASC, dueDate ASC, createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY priority ASC, dueDate ASC")
    fun getTasksByStatus(status: Int): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId ORDER BY isCompleted ASC, status ASC, priority ASC")
    fun getTasksByCategory(categoryId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :startOfDay AND :endOfDay ORDER BY isCompleted ASC, dueDate ASC")
    fun getTasksForDay(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :start AND :end ORDER BY isCompleted ASC, dueDate ASC")
    fun getTasksForDateRange(start: Long, end: Long): Flow<List<TaskEntity>>

    @Query("UPDATE tasks SET status = :status, isCompleted = :completed, completedAt = :completedAt WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: Long, status: Int, completed: Boolean, completedAt: Long?)
    
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)
    
    // Delete completed tasks older than given timestamp
    @Query("DELETE FROM tasks WHERE isCompleted = 1 AND completedAt < :beforeTimestamp")
    suspend fun deleteOldCompletedTasks(beforeTimestamp: Long)
}

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Long): CategoryEntity?
}
