package com.example.quest.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.quest.domain.model.Task
import com.example.quest.ui.screens.home.HomeScreen
import com.example.quest.ui.screens.calendar.CalendarScreen
import com.example.quest.ui.screens.tasks.CreateTaskScreen
import com.example.quest.ui.screens.tasks.CreateTaskData

//routes
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Calendar : Screen("calendar")
    object CreateTask : Screen("create_task")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    onTaskComplete: (Task) -> Unit,
    onCreateTask: (CreateTaskData) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(200)) },
        exitTransition = { fadeOut(animationSpec = tween(200)) }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                tasks = tasks,
                onTaskClick = onTaskClick,
                onTaskComplete = onTaskComplete
            )
        }
        
        composable(Screen.Calendar.route) {
            CalendarScreen(
                tasks = tasks,
                onTaskClick = onTaskClick,
                onTaskComplete = onTaskComplete
            )
        }
        
        composable(
            route = Screen.CreateTask.route,
            enterTransition = { slideInVertically(initialOffsetY = { it }) },
            exitTransition = { slideOutVertically(targetOffsetY = { it }) }
        ) {
            CreateTaskScreen(
                onSave = { data ->
                    onCreateTask(data)
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }
    }
}
