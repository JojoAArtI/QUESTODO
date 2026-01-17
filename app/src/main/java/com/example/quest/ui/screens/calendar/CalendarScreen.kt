package com.example.quest.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quest.domain.model.Task
import com.example.quest.ui.components.TaskCard
import com.example.quest.ui.theme.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * Neo-Brutalism Calendar Screen with Visual Calendar Grid
 */
@Composable
fun CalendarScreen(
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    onTaskComplete: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    // Tasks for selected date
    val tasksForDate = tasks.filter { task ->
        task.dueDate?.toLocalDate() == selectedDate
    }
    
    // Dates with tasks in current month
    val datesWithTasks = tasks.mapNotNull { it.dueDate?.toLocalDate() }
        .filter { it.month == currentMonth.month && it.year == currentMonth.year }
        .toSet()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CardWhite
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 56.dp, bottom = 16.dp)
            ) {
                // Month Navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { currentMonth = currentMonth.minusMonths(1) },
                        modifier = Modifier
                            .size(40.dp)
                            .border(2.dp, BorderDark, CircleShape)
                            .background(LightGray, CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous month",
                            tint = TextDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Text(
                        text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    
                    IconButton(
                        onClick = { currentMonth = currentMonth.plusMonths(1) },
                        modifier = Modifier
                            .size(40.dp)
                            .border(2.dp, BorderDark, CircleShape)
                            .background(LightGray, CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next month",
                            tint = TextDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Calendar Grid
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Shadow
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 4.dp, y = 4.dp)
                    .background(BorderDark, RoundedCornerShape(16.dp))
            )
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, BorderDark, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = CardWhite
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Day of week headers
                    Row(modifier = Modifier.fillMaxWidth()) {
                        DayOfWeek.values().forEach { day ->
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Calendar days
                    val firstDayOfMonth = currentMonth.atDay(1)
                    val lastDayOfMonth = currentMonth.atEndOfMonth()
                    val startOffset = (firstDayOfMonth.dayOfWeek.value % 7)
                    
                    val days = buildList {
                        // Empty slots before first day
                        repeat(startOffset) { add(null) }
                        // Actual days
                        for (day in 1..lastDayOfMonth.dayOfMonth) {
                            add(currentMonth.atDay(day))
                        }
                    }
                    
                    // Chunked into weeks
                    days.chunked(7).forEach { week ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            week.forEach { date ->
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (date != null) {
                                        CalendarDay(
                                            date = date,
                                            isSelected = date == selectedDate,
                                            isToday = date == LocalDate.now(),
                                            hasTask = date in datesWithTasks,
                                            onClick = { selectedDate = date }
                                        )
                                    }
                                }
                            }
                            // Fill remaining if week is incomplete
                            repeat(7 - week.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Selected date header
        Text(
            text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextDark,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Tasks for selected date
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                bottom = 100.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (tasksForDate.isEmpty()) {
                item {
                    EmptyDateState()
                }
            } else {
                items(tasksForDate, key = { it.id }) { task ->
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
private fun CalendarDay(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    hasTask: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = when {
                    isSelected -> 2.dp
                    isToday -> 1.5.dp
                    else -> 0.dp
                },
                color = when {
                    isSelected -> BorderDark
                    isToday -> ForestGreen
                    else -> CardWhite
                },
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                when {
                    isSelected -> SunflowerYellow
                    isToday -> LightGreen
                    else -> CardWhite
                }
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                color = TextDark,
                fontSize = 14.sp
            )
            
            // Task indicator dot
            if (hasTask && !isSelected) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .background(ForestGreen, CircleShape)
                )
            }
        }
    }
}

@Composable
private fun EmptyDateState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No tasks scheduled",
            style = MaterialTheme.typography.titleMedium,
            color = TextMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Tap + to add a new task",
            style = MaterialTheme.typography.bodyMedium,
            color = TextLight,
            textAlign = TextAlign.Center
        )
    }
}
