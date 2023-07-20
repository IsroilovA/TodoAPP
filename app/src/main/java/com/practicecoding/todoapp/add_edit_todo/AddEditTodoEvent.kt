package com.practicecoding.todoapp.add_edit_todo

sealed class AddEditTodoEvent{
    object OnSaveTodoClick: AddEditTodoEvent()
    data class SetTitle(val title: String): AddEditTodoEvent()
    data class SetDescription(val description: String): AddEditTodoEvent()

}