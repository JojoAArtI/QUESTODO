package com.example.quest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.quest.ui.theme.*

enum class NavDestination(
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String
) {
    HOME(Icons.Outlined.Home, Icons.Filled.Home, "Home"),
    ADD(Icons.Outlined.Add, Icons.Filled.Add, "Add"),
    CALENDAR(Icons.Outlined.CalendarMonth, Icons.Filled.CalendarMonth, "Calendar")
}

/**
 * Neo-Brutalism Bottom Navigation Bar - Home, Add, Calendar
 */
@Composable
fun AppBottomNavBar(
    currentDestination: NavDestination,
    onDestinationSelected: (NavDestination) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 3.dp, y = 3.dp)
                .background(BorderDark, RoundedCornerShape(24.dp))
        )
        
        // Main bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, BorderDark, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            color = CardWhite
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home
                NavItem(
                    destination = NavDestination.HOME,
                    isSelected = currentDestination == NavDestination.HOME,
                    onClick = { onDestinationSelected(NavDestination.HOME) }
                )
                
                // Add Button (center)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .offset(y = (-4).dp)
                ) {
                    // Shadow
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .offset(x = 3.dp, y = 3.dp)
                            .background(BorderDark, CircleShape)
                    )
                    
                    FloatingActionButton(
                        onClick = onAddClick,
                        modifier = Modifier
                            .fillMaxSize()
                            .border(2.dp, BorderDark, CircleShape),
                        shape = CircleShape,
                        containerColor = SunflowerYellow,
                        contentColor = TextDark,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Add Task",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                
                // Calendar
                NavItem(
                    destination = NavDestination.CALENDAR,
                    isSelected = currentDestination == NavDestination.CALENDAR,
                    onClick = { onDestinationSelected(NavDestination.CALENDAR) }
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    destination: NavDestination,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) LightGreen else CardWhite)
            .border(if (isSelected) 2.dp else 0.dp, if (isSelected) BorderDark else CardWhite, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isSelected) destination.selectedIcon else destination.icon,
            contentDescription = destination.label,
            tint = if (isSelected) ForestGreen else TextMedium,
            modifier = Modifier.size(26.dp)
        )
    }
}

/**
 * Status Filter Tabs - includes Done filter
 */
@Composable
fun StatusFilterTabs(
    selectedStatus: String?,
    onStatusSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val statuses = listOf(
        null to "All",
        "TODO" to "To Do",
        "IN_PROGRESS" to "In Progress",
        "DONE" to "Done"
    )
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        statuses.forEach { (status, label) ->
            val isSelected = selectedStatus == status
            val color = when (status) {
                "TODO" -> StatusToDo
                "IN_PROGRESS" -> StatusInProgress
                "DONE" -> StatusDone
                else -> ForestGreen
            }
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) BorderDark else BorderMedium,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(if (isSelected) color else CardWhite)
                    .clickable { onStatusSelected(status) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) TextDark else TextMedium
                )
            }
        }
    }
}
