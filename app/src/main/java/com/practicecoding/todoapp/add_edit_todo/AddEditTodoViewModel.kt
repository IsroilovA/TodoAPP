package com.practicecoding.todoapp.add_edit_todo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicecoding.todoapp.util.UiEvent
import com.practicecoding.todoapp.data.Todo
import com.practicecoding.todoapp.data.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTodoViewModel @Inject constructor(
    private val repository: TodoRepository,
    savedStateHandle: SavedStateHandle
):ViewModel() {
    //states
    var todo by mutableStateOf<Todo?>(null)
        private set
    var title by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set

    //one time ui events
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    init {
        val todoId = savedStateHandle.get<Int>("todoId")!!
        if (todoId != -1) {
            viewModelScope.launch {
                repository.getTodoById(todoId)?.let { todo ->
                    title = todo.title
                    description = todo.description ?: ""
                    this@AddEditTodoViewModel.todo = todo
                }
            }
        }
    }
    fun onEvent(event: AddEditTodoEvent) {
        when (event) {
            AddEditTodoEvent.OnSaveTodoClick -> {
                //validate user input
                if (title.isBlank()) {
                    viewModelScope.launch {
                        _uiEvent.send(UiEvent.ShowSnackbar("Title cannot be empty"))
                    }
                    return
                }
                //insert into database
                viewModelScope.launch {
                    repository.upsertTodo(
                        Todo(
                            title = title,
                            description = description,
                            isDone = todo?.isDone ?: false,
                            id = todo?.id
                        )
                    )
                }
                viewModelScope.launch{
                    _uiEvent.send(UiEvent.PopBackStack)
                }
            }
            is AddEditTodoEvent.SetDescription -> {
                description = event.description
            }
            is AddEditTodoEvent.SetTitle -> {
                title = event.title
            }
        }
    }
}