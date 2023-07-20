package com.practicecoding.todoapp

import com.practicecoding.todoapp.data.Todo

data class TodoState(
    val todos: List<Todo> = emptyList(),
    val title: String = "",
    val description: String = "",
    val isDone: Boolean = false
)