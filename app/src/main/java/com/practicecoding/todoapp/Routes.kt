package com.practicecoding.todoapp

sealed class Routes(val route: String) {
    object TodoListScreen: Routes("todo_list")
    object AddEditScreen: Routes("add_edit_todo")
}