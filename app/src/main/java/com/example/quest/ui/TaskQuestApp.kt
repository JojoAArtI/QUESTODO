package com.example.quest.ui

import android.app.Application
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.quest.navigation.AppNavHost
import com.example.quest.navigation.Screen
import com.example.quest.ui.components.AppBottomNavBar
import com.example.quest.ui.components.NavDestination
import com.example.quest.ui.theme.CreamBackground
import com.example.quest.ui.theme.QuestTheme

@Composable
fun TaskQuestApp(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Request permission on start
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
           // Good
        }
    }
    
    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val viewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory(context.applicationContext as Application)
    )
    
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Map route to NavDestination
    val currentDestination = when (currentRoute) {
        Screen.Home.route -> NavDestination.HOME
        Screen.Calendar.route -> NavDestination.CALENDAR
        else -> NavDestination.HOME
    }
    
    // Show bottom bar on main screens
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Calendar.route
    )
    
    // Collect tasks from ViewModel
    val tasks by viewModel.tasks.collectAsState()
    
    QuestTheme {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(CreamBackground)
        ) {
            // Main content
            AppNavHost(
                navController = navController,
                tasks = tasks,
                onTaskClick = { task ->
                    // Cycle status on click
                    viewModel.cycleTaskStatus(task)
                },
                onTaskComplete = { task ->
                    // Toggle completion
                    viewModel.toggleTaskCompletion(task)
                },
                onTaskDelete = { task ->
                    viewModel.deleteTask(task)
                },
                onCreateTask = { taskData ->
                    viewModel.createTask(taskData)
                },
                modifier = Modifier.fillMaxSize()
            )
            
            // Bottom Navigation
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                AppBottomNavBar(
                    currentDestination = currentDestination,
                    onDestinationSelected = { destination ->
                        val route = when (destination) {
                            NavDestination.HOME -> Screen.Home.route
                            NavDestination.ADD -> return@AppBottomNavBar
                            NavDestination.CALENDAR -> Screen.Calendar.route
                        }
                        
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    onAddClick = {
                        navController.navigate(Screen.CreateTask.route)
                    }
                )
            }
        }
    }
}
