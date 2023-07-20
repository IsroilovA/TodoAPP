package com.practicecoding.todoapp

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.practicecoding.todoapp.todo_list.TodoListScreen

@Composable
fun Navigation(
    todoListScreen: Composable,
    addEditScreen: Composable,
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.TodoListScreen.route){
        composable(route = Routes.TodoListScreen.route){
            todoListScreen
        }
        composable(route = Routes.AddEditScreen.route){
            addEditScreen
        }
    }
}