package com.practicecoding.todoapp.add_edit_todo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicecoding.todoapp.TodoState
import com.practicecoding.todoapp.util.UiEvent
import com.practicecoding.todoapp.data.Todo
import com.practicecoding.todoapp.data.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTodoViewModel @Inject constructor(
    private val repository: TodoRepository,
    savedStateHandle: SavedStateHandle
):ViewModel() {
    //states
    private val _state = MutableStateFlow(TodoState())
    val state = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), TodoState())
    private val _uiEvent = Channel<UiEvent>()
    private val userId:Int? = savedStateHandle["todoId"]
    val uiEvent = _uiEvent.receiveAsFlow()
    fun onEvent(event: AddEditTodoEvent) {
        when (event) {
            AddEditTodoEvent.OnSaveTodoClick -> {
                if (userId != -1){
                    viewModelScope.launch {
                        val todo = userId?.let { repository.getTodoById(it) }
                        if (todo != null) {
                            _state.update { it.copy(
                                title = todo.title,
                                description = todo.description,
                                isDone = todo.isDone
                            ) }
                        }
                    }
                }
                val title = state.value.title
                val description = state.value.description
                val isDone = state.value.isDone
                //validate user input
                if (title.isBlank()) {
                    viewModelScope.launch {
                        _uiEvent.send(UiEvent.ShowSnackbar("Title cannot be empty"))
                    }
                    return
                }
                val todo = Todo(
                    title = title,
                    description = description,
                    isDone = isDone
                )
                //insert into database
                viewModelScope.launch {
                    repository.upsertTodo(todo)
                }
                //clear states
                _state.update {
                    it.copy(
                        title = "",
                        description = "",
                        isDone = false
                    )
                }
                viewModelScope.launch{
                    _uiEvent.send(UiEvent.PopBackStack)
                }
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