package com.practicecoding.todoapp.todo_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicecoding.todoapp.util.Routes
import com.practicecoding.todoapp.util.UiEvent
import com.practicecoding.todoapp.data.Todo
import com.practicecoding.todoapp.data.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val repository: TodoRepository
): ViewModel() {
    //state
    val todos = repository.getTodos()
    //cash up deleted
    private var deletedTodo: Todo? = null
    //ui One time events
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
                    repository.deleteTodo(event.todo)
                    _uiEvent.send(
                        UiEvent.ShowSnackbar(
                        message = "Todo deleted",
                        action = "Undo"
                    ))
                }
            }
            is TodoListEvent.OnDoneChange -> {
                viewModelScope.launch {
                    repository.upsertTodo(event.todo.copy(isDone = event.isDone))
                }
            }
            TodoListEvent.UndoDelete -> {
                deletedTodo?.let {
                    viewModelScope.launch { repository.upsertTodo(it) }
                }
            }

            is TodoListEvent.OnTodoClick -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Navigate(Routes.AddEditTodoScreen.route + "?todoId=${event.todo.id}"))
                }
            }
        }
    }
}