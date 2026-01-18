package com.example.quest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quest.data.local.entity.TaskPriority
import com.example.quest.data.local.entity.TaskStatus
import com.example.quest.domain.model.Task
import com.example.quest.ui.theme.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Neo-Brutalism Task Card - Shows completed tasks with strikethrough
 */
@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    
    val priorityColor = when (task.priority) {
        TaskPriority.HIGH -> PriorityHigh
        TaskPriority.MEDIUM -> PriorityMedium
        TaskPriority.LOW -> PriorityLow
    }
    
    // Neo-brutalism card with thick border and offset shadow
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp, end = 4.dp)
    ) {
        // Shadow layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 4.dp, y = 4.dp)
                .background(BorderDark, RoundedCornerShape(12.dp))
        )
        
        // Main card - slightly faded if completed
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, BorderDark, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .clickable { onClick() },
            color = if (task.isCompleted) LightGray else CardWhite
        ) {
            Column {
                // Priority color bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(if (task.isCompleted) TextLight else priorityColor)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Checkbox
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .border(2.dp, BorderDark, RoundedCornerShape(6.dp))
                            .background(if (task.isCompleted) StatusDone else CardWhite)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onComplete()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (task.isCompleted) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Completed",
                                tint = CardWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Content
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = if (task.isCompleted) TextLight else TextDark,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                        )
                        
                        // Description
                        task.description?.let { desc ->
                            if (desc.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (task.isCompleted) TextLight else TextMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                                )
                            }
                        }
                        
                        // Tags row
                        if (!task.isCompleted) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Status badge
                                StatusBadge(status = task.status)
                                
                                // Due date
                                task.dueDate?.let { due ->
                                    DueDateBadge(dueDate = due, isOverdue = task.isOverdue)
                                }
                            }
                        } else {
                            // Completed badge
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "✓ Completed",
                                style = MaterialTheme.typography.labelSmall,
                                color = StatusDone,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Delete",
                            tint = TextMedium.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: TaskStatus) {
    val color = when (status) {
        TaskStatus.TODO -> StatusToDo
        TaskStatus.IN_PROGRESS -> StatusInProgress
        TaskStatus.DONE -> StatusDone
    }
    
    Box(
        modifier = Modifier
            .border(1.5.dp, BorderDark, RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelSmall,
            color = TextDark,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp
        )
    }
}

@Composable
fun DueDateBadge(dueDate: LocalDateTime, isOverdue: Boolean) {
    val dateText = formatDueDate(dueDate)
    val color = if (isOverdue) CoralRed else TextMedium
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Schedule,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = color
        )
        Text(
            text = dateText,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontSize = 10.sp
        )
    }
}

private fun formatDueDate(dueDate: LocalDateTime): String {
    val today = LocalDate.now()
    val tomorrow = today.plusDays(1)
    val dueDay = dueDate.toLocalDate()
    
    return when {
        dueDay == today -> "Today"
        dueDay == tomorrow -> "Tomorrow"
        dueDay.isBefore(today) -> "Overdue"
        else -> dueDate.format(DateTimeFormatter.ofPattern("MMM d"))
    }
}
