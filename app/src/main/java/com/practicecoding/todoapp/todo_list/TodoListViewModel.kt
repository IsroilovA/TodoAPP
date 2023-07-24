package com.practicecoding.todoapp.todo_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicecoding.todoapp.util.Routes
import com.practicecoding.todoapp.TodoState
import com.practicecoding.todoapp.util.UiEvent
import com.practicecoding.todoapp.data.Todo
import com.practicecoding.todoapp.data.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val repository: TodoRepository
): ViewModel() {
    //states
    private val _todos = repository.getTodos().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
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
                    val todo = repository.getTodoById(event.todo.id)
                    if (todo != null) {
                        _todoState.update {
                            it.copy(
                                title = todo.title,
                                description = todo.description,
                                isDone = todo.isDone
                            )
                        }
                    }
                    _uiEvent.send(UiEvent.Navigate(Routes.AddEditTodoScreen.route + "?todoId=${event.todo.id}"))
                }
            }
        }
    }
}