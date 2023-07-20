package com.practicecoding.todoapp.todo_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.practicecoding.todoapp.Routes
import com.practicecoding.todoapp.TodoState
import com.practicecoding.todoapp.data.Todo
import com.practicecoding.todoapp.data.TodoDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoListViewModel(
    private val dao:TodoDao
): ViewModel() {
    var navController: NavController ?=null
    //states
    private val _todos = dao.getTodos().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _todoState = MutableStateFlow(TodoState())
    //combine two states
    val todoState = combine(_todoState, _todos){ todoState, todos ->
        todoState.copy(
            todos = todos
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), TodoState())
    //cash up deleted
    private var deletedTodo: Todo? = null
    fun onEvent(event: TodoListEvent){
        when(event){
            TodoListEvent.AddTodo -> {
                navController?.navigate(Routes.AddEditTodoScreen.route)
            }
            is TodoListEvent.DeleteTodo -> {
                viewModelScope.launch {
                    deletedTodo = event.todo
                    dao.deleteTodo(event.todo)
                }
            }
            is TodoListEvent.OnDoneChange -> {
                viewModelScope.launch {
                    dao.upsertTodo(event.todo.copy(isDone = event.isDone))
                }
            }
            TodoListEvent.UndoDelete -> {
                viewModelScope.launch {
                    deletedTodo?.let { dao.upsertTodo(it) }
                }
            }
        }
    }
}