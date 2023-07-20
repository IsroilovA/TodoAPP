package com.practicecoding.todoapp.todo_list

import com.practicecoding.todoapp.data.Todo

sealed interface TodoListEvent{
    data class DeleteTodo(val todo: Todo): TodoListEvent
    data class OnDoneChange(val todo: Todo, val isDone: Boolean): TodoListEvent
    object UndoDelete: TodoListEvent
    object AddTodo: TodoListEvent
}