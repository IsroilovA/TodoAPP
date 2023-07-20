package com.practicecoding.todoapp

sealed class Routes(val route: String) {
    object TodoListScreen: Routes("Todo_list")
    object AddEditTodoScreen: Routes("Add_edit_todo")
}