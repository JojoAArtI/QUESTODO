package com.example.quest.domain.model

import com.example.quest.data.local.entity.TaskPriority
import com.example.quest.data.local.entity.TaskStatus
import com.example.quest.data.local.entity.RecurringType
import java.time.LocalDateTime

// diff cats for org
data class Category(
    val id: Long = 0,
    val name: String,
    val colorHex: String = "#6B9B6B",
    val iconName: String = "folder"
)

// core model
data class Task(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val notes: String? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: TaskStatus = TaskStatus.TODO,
    val category: Category? = null,
    val categoryId: Long? = null,
    val dueDate: LocalDateTime? = null,
    val reminderTime: LocalDateTime? = null,
    val recurringType: RecurringType = RecurringType.NONE,
    val isCompleted: Boolean = false,
    val completedAt: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    val isOverdue: Boolean get() = dueDate?.let { 
        it.isBefore(LocalDateTime.now()) && !isCompleted 
    } ?: false
}
