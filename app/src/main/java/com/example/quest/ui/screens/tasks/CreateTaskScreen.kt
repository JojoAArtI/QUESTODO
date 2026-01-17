package com.example.quest.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quest.data.local.entity.TaskPriority
import com.example.quest.data.local.entity.TaskStatus
import com.example.quest.data.local.entity.RecurringType
import com.example.quest.ui.theme.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Create task data - simplified, no subtasks
 */
data class CreateTaskData(
    val title: String,
    val description: String?,
    val notes: String?,
    val priority: TaskPriority,
    val status: TaskStatus,
    val dueDate: LocalDateTime?,
    val recurringType: RecurringType,
    val categoryId: Long?
)

/**
 * Neo-Brutalism Create Task Screen - No subtasks
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    onSave: (CreateTaskData) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var status by remember { mutableStateOf(TaskStatus.TODO) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var recurringType by remember { mutableStateOf(RecurringType.NONE) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        // Top Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CardWhite
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(top = 48.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCancel) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Cancel",
                        tint = TextMedium
                    )
                }
                
                Text(
                    text = "New Task",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                
                // Save button
                Box {
                    Box(
                        modifier = Modifier
                            .offset(x = 2.dp, y = 2.dp)
                            .background(BorderDark, RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Create", color = BorderDark)
                    }
                    
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .border(2.dp, BorderDark, RoundedCornerShape(8.dp))
                            .clickable(enabled = title.isNotBlank()) {
                                val dueDateTime = selectedDate?.let { 
                                    LocalDateTime.of(it, LocalTime.NOON) 
                                }
                                onSave(
                                    CreateTaskData(
                                        title = title.trim(),
                                        description = description.trim().ifBlank { null },
                                        notes = notes.trim().ifBlank { null },
                                        priority = priority,
                                        status = status,
                                        dueDate = dueDateTime,
                                        recurringType = recurringType,
                                        categoryId = null
                                    )
                                )
                            },
                        color = if (title.isNotBlank()) SageGreen else LightGray
                    ) {
                        Text(
                            "Create",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = if (title.isNotBlank()) TextOnDark else TextMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title input
            NeoTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = "Task name",
                modifier = Modifier.fillMaxWidth()
            )
            
            // Description
            NeoTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = "Description",
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            
            // Priority Section
            SectionHeader("Priority")
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PriorityChip("High", TaskPriority.HIGH, priority, PriorityHigh) { priority = it }
                PriorityChip("Medium", TaskPriority.MEDIUM, priority, PriorityMedium) { priority = it }
                PriorityChip("Low", TaskPriority.LOW, priority, PriorityLow) { priority = it }
            }
            
            // Status Section
            SectionHeader("Status")
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusChip("To Do", TaskStatus.TODO, status, StatusToDo) { status = it }
                StatusChip("In Progress", TaskStatus.IN_PROGRESS, status, StatusInProgress) { status = it }
            }
            
            // Due Date Section
            SectionHeader("Due Date")
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DateChip("Today", LocalDate.now(), selectedDate) { selectedDate = it }
                DateChip("Tomorrow", LocalDate.now().plusDays(1), selectedDate) { selectedDate = it }
                DateChip("Next Week", LocalDate.now().plusWeeks(1), selectedDate) { selectedDate = it }
            }
            
            NeoButton(
                text = selectedDate?.format(DateTimeFormatter.ofPattern("MMM d, yyyy")) ?: "Pick a date",
                onClick = { showDatePicker = true },
                icon = Icons.Outlined.CalendarMonth
            )
            
            // Recurring Section
            SectionHeader("Repeat")
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RecurringChip("None", RecurringType.NONE, recurringType) { recurringType = it }
                RecurringChip("Daily", RecurringType.DAILY, recurringType) { recurringType = it }
                RecurringChip("Weekly", RecurringType.WEEKLY, recurringType) { recurringType = it }
                RecurringChip("Monthly", RecurringType.MONTHLY, recurringType) { recurringType = it }
            }
            
            // Notes Section
            SectionHeader("Notes")
            NeoTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = "Add notes...",
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = (selectedDate ?: LocalDate.now())
                .toEpochDay() * 86400000
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = java.time.Instant.ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = ForestGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = TextMedium)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = ForestGreen,
                    todayDateBorderColor = SunflowerYellow
                )
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = TextMedium,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun NeoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 3.dp, y = 3.dp)
                .background(BorderDark, RoundedCornerShape(8.dp))
        )
        
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, BorderDark, RoundedCornerShape(8.dp))
                .background(CardWhite, RoundedCornerShape(8.dp))
                .padding(12.dp),
            textStyle = TextStyle(
                color = TextDark,
                fontSize = 16.sp
            ),
            cursorBrush = SolidColor(ForestGreen),
            minLines = minLines,
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = TextLight,
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
private fun NeoButton(
    text: String,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 2.dp, y = 2.dp)
                .background(BorderDark, RoundedCornerShape(8.dp))
        )
        
        Surface(
            modifier = Modifier
                .border(2.dp, BorderDark, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .clickable { onClick() },
            color = LightGray
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                icon?.let {
                    Icon(it, null, tint = TextMedium, modifier = Modifier.size(18.dp))
                }
                Text(text, color = TextDark)
            }
        }
    }
}

@Composable
private fun PriorityChip(
    label: String,
    value: TaskPriority,
    selected: TaskPriority,
    color: androidx.compose.ui.graphics.Color,
    onSelect: (TaskPriority) -> Unit
) {
    val isSelected = selected == value
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) BorderDark else BorderMedium,
                shape = RoundedCornerShape(8.dp)
            )
            .background(if (isSelected) color.copy(alpha = 0.3f) else CardWhite)
            .clickable { onSelect(value) }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) TextDark else TextMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun StatusChip(
    label: String,
    value: TaskStatus,
    selected: TaskStatus,
    color: androidx.compose.ui.graphics.Color,
    onSelect: (TaskStatus) -> Unit
) {
    val isSelected = selected == value
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) BorderDark else BorderMedium,
                shape = RoundedCornerShape(8.dp)
            )
            .background(if (isSelected) color else CardWhite)
            .clickable { onSelect(value) }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextDark,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun DateChip(
    label: String,
    date: LocalDate,
    selected: LocalDate?,
    onSelect: (LocalDate) -> Unit
) {
    val isSelected = selected == date
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) BorderDark else BorderMedium,
                shape = RoundedCornerShape(8.dp)
            )
            .background(if (isSelected) SunflowerYellow else CardWhite)
            .clickable { onSelect(date) }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextDark,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun RecurringChip(
    label: String,
    value: RecurringType,
    selected: RecurringType,
    onSelect: (RecurringType) -> Unit
) {
    val isSelected = selected == value
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) BorderDark else BorderMedium,
                shape = RoundedCornerShape(8.dp)
            )
            .background(if (isSelected) LightGreen else CardWhite)
            .clickable { onSelect(value) }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) ForestGreen else TextMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
