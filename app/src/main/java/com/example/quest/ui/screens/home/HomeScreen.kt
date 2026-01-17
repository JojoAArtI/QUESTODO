package com.example.quest.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.quest.data.local.entity.TaskStatus
import com.example.quest.domain.model.Task
import com.example.quest.ui.components.TaskCard
import com.example.quest.ui.components.StatusFilterTabs
import com.example.quest.ui.theme.*

/**
 * Neo-Brutalism Home Screen with Board View
 */
@Composable
fun HomeScreen(
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    onTaskComplete: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    
    val filteredTasks = when (selectedStatus) {
        "TODO" -> tasks.filter { it.status == TaskStatus.TODO && !it.isCompleted }
        "IN_PROGRESS" -> tasks.filter { it.status == TaskStatus.IN_PROGRESS && !it.isCompleted }
        "DONE" -> tasks.filter { it.isCompleted }
        else -> tasks
    }
    
    val todoCount = tasks.count { it.status == TaskStatus.TODO && !it.isCompleted }
    val inProgressCount = tasks.count { it.status == TaskStatus.IN_PROGRESS && !it.isCompleted }
    val doneCount = tasks.count { it.isCompleted }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardWhite)
                .padding(horizontal = 20.dp)
                .padding(top = 56.dp, bottom = 16.dp)
        ) {
            Text(
                text = "My Tasks",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            StatusFilterTabs(
                selectedStatus = selectedStatus,
                onStatusSelected = { selectedStatus = it }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("To Do", todoCount, LightYellow, TextDark, Modifier.weight(1f))
                StatCard("Doing", inProgressCount, LightBlue, TextDark, Modifier.weight(1f))
                StatCard("Done", doneCount, LightGreen, TextDark, Modifier.weight(1f))
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredTasks.isEmpty()) {
                item {
                    EmptyState(statusFilter = selectedStatus)
                }
            } else {
                items(filteredTasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onClick = { onTaskClick(task) },
                        onComplete = { onTaskComplete(task) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    count: Int,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 2.dp, y = 2.dp)
                .background(BorderDark, RoundedCornerShape(8.dp))
        )
        
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, BorderDark, RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(8.dp),
            color = backgroundColor
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    statusFilter: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = SageGreen.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = when (statusFilter) {
                "TODO" -> "Inbox zero? Sus."
                "IN_PROGRESS" -> "Slacking off?"
                "DONE" -> "Do some actual work."
                else -> "Nothing to do? \nGo touch grass."
            },
            style = MaterialTheme.typography.titleMedium,
            color = TextDark,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Tap + to pretend you're busy",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMedium,
            textAlign = TextAlign.Center
        )
    }
}
