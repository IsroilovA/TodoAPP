package com.practicecoding.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.practicecoding.todoapp.add_edit_todo.AddEditTodoScreen
import com.practicecoding.todoapp.add_edit_todo.AddEditTodoViewModel
import com.practicecoding.todoapp.todo_list.TodoListScreen
import com.practicecoding.todoapp.todo_list.TodoListViewModel
import com.practicecoding.todoapp.ui.theme.ToDoAppTheme
import com.practicecoding.todoapp.util.Routes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val todoListViewModel: TodoListViewModel by viewModels()
    private val addEditTodoViewModel: AddEditTodoViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoAppTheme {
                //navController instance
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Routes.TodoListScreen.route
                ) {
                    composable(route = Routes.TodoListScreen.route) {
                        TodoListScreen(
                            onNavigate = { navController.navigate(it.route) },
                            todoListViewModel
                        )
                    }
                    composable(
                        route = Routes.AddEditTodoScreen.route + "?todoId={todoId}",
                        arguments =listOf(
                            navArgument(name = "todoId") {
                                type = NavType.IntType
                                defaultValue = -1
                            }
                        )
                    ) {
                        AddEditTodoScreen(
                            onPopBackStack = {navController.popBackStack()},
                            addEditTodoViewModel
                        )
                    }
                }
            }
        }
    }
}
