package com.practicecoding.todoapp.add_edit_todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.practicecoding.todoapp.TodoState
import com.practicecoding.todoapp.data.Todo
import com.practicecoding.todoapp.data.TodoDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddEditTodoViewModel(
    private val dao: TodoDao
):ViewModel() {
    var navController: NavController?=null
    //states
    private val _state = MutableStateFlow(TodoState())
    val state = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), TodoState())
    fun onEvent(event: AddEditTodoEvent) {
        when (event) {
            AddEditTodoEvent.OnSaveTodoClick -> {
                val title = state.value.title
                val description = state.value.description
                val isDone = state.value.isDone
                //validate user input
                if (title.isBlank()) {
                    return
                }
                val todo = Todo(
                    title = title,
                    description = description,
                    isDone = isDone
                )
                //insert into database
                viewModelScope.launch {
                    dao.upsertTodo(todo)
                }
                //clear states
                _state.update {
                    it.copy(
                        title = "",
                        description = "",
                        isDone = false
                    )
                }
                navController?.popBackStack()
            }
            is AddEditTodoEvent.SetDescription -> {
                _state.update { it.copy(
                    description = event.description
                ) }
            }
            is AddEditTodoEvent.SetTitle -> {
                _state.update { it.copy(
                    title = event.title
                ) }
            }
        }
    }
}