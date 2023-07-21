package com.practicecoding.todoapp.todo_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicecoding.todoapp.util.Routes
import com.practicecoding.todoapp.TodoState
import com.practicecoding.todoapp.util.UiEvent
import com.practicecoding.todoapp.data.Todo
import com.practicecoding.todoapp.data.TodoDao
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoListViewModel(
    private val dao:TodoDao
): ViewModel() {
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
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    fun onEvent(event: TodoListEvent){
        when(event){
            TodoListEvent.AddTodo -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Navigate(Routes.AddEditTodoScreen.route))
                }
            }
            is TodoListEvent.DeleteTodo -> {
                viewModelScope.launch {
                    deletedTodo = event.todo
                    dao.deleteTodo(event.todo)
                    _uiEvent.send(
                        UiEvent.ShowSnackbar(
                        message = "Todo deleted",
                        action = "Undo"
                    ))
                }
            }
            is TodoListEvent.OnDoneChange -> {
                viewModelScope.launch {
                    dao.upsertTodo(event.todo.copy(isDone = event.isDone))
                }
            }
            TodoListEvent.UndoDelete -> {
                deletedTodo?.let {
                    viewModelScope.launch { dao.upsertTodo(it) }
                }
            }
        }
    }
}