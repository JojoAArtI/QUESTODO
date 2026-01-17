package com.example.quest.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quest.data.local.TaskQuestDatabase
import com.example.quest.data.local.entity.TaskPriority
import com.example.quest.data.local.entity.TaskStatus
import com.example.quest.data.local.entity.RecurringType
import com.example.quest.data.repository.TaskRepository
import com.example.quest.domain.model.Task
import com.example.quest.ui.screens.tasks.CreateTaskData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = TaskQuestDatabase.getDatabase(application)
    private val repository = TaskRepository(database.taskDao())
    
    // The source of truth. Don't mess it up.
    val tasks: StateFlow<List<Task>> = repository.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun createTask(data: CreateTaskData) {
        viewModelScope.launch {
            val task = Task(
                title = data.title,
                description = data.description,
                notes = data.notes,
                priority = data.priority,
                status = data.status,
                dueDate = data.dueDate,
                recurringType = data.recurringType,
                categoryId = data.categoryId
            )
            repository.insertTask(task)
            
            // Ping! Reminder set.
            com.example.quest.util.NotificationScheduler.schedule(getApplication(), task)
        }
    }
    
    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            val newCompleted = !task.isCompleted
            // If it's done, it's done. if not, back to the pile.
            val updatedTask = task.copy(
                isCompleted = newCompleted,
                status = if (newCompleted) TaskStatus.DONE else TaskStatus.TODO,
                completedAt = if (newCompleted) LocalDateTime.now() else null
            )
            repository.updateTask(updatedTask)
            
            if (newCompleted) {
                com.example.quest.util.NotificationScheduler.cancel(getApplication(), task)
            } else {
                com.example.quest.util.NotificationScheduler.schedule(getApplication(), task)
            }
        }
    }
    
    fun cycleTaskStatus(task: Task) {
        viewModelScope.launch {
            // Spin the wheel of productivity
            val (newStatus, newCompleted) = when (task.status) {
                TaskStatus.TODO -> TaskStatus.IN_PROGRESS to false
                TaskStatus.IN_PROGRESS -> TaskStatus.DONE to true
                TaskStatus.DONE -> TaskStatus.TODO to false
            }
            
            val updatedTask = task.copy(
                status = newStatus,
                isCompleted = newCompleted,
                completedAt = if (newCompleted) LocalDateTime.now() else null
            )
            repository.updateTask(updatedTask)
            
             if (newCompleted) {
                com.example.quest.util.NotificationScheduler.cancel(getApplication(), task)
            } else {
                com.example.quest.util.NotificationScheduler.schedule(getApplication(), task)
            }
        }
    }
    
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            // Yeet the task into the void
            repository.deleteTask(task)
            com.example.quest.util.NotificationScheduler.cancel(getApplication(), task)
        }
    }
    
    fun cleanupOldCompletedTasks() {
        viewModelScope.launch {
            val yesterday = LocalDateTime.now().minusDays(1)
            // Garbage collection for human tasks
            tasks.value
                .filter { it.isCompleted && it.completedAt?.isBefore(yesterday) == true }
                .forEach { repository.deleteTask(it) }
        }
    }
}

class TaskViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class. Do better.")
    }
}
