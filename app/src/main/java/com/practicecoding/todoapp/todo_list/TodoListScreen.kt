package com.practicecoding.todoapp.todo_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.practicecoding.todoapp.util.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val todos by viewModel.todos.collectAsState(initial = emptyList())
    val snackbarHostState = remember{ SnackbarHostState() }
    LaunchedEffect(key1 = true){
        viewModel.uiEvent.collect{event->
            when(event){
                is UiEvent.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        event.message,
                        actionLabel = event.action
                    )
                    if(result == SnackbarResult.ActionPerformed){
                        viewModel.onEvent(TodoListEvent.UndoDelete)
                    }
                }
                is UiEvent.Navigate -> {
                    onNavigate(event)
                }
                else -> Unit
            }
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(TodoListEvent.AddTodo)
                }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add todo")
            }
        },
        modifier = Modifier.padding(16.dp)
    ) {padding->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ){
            items(todos.asReversed()){todo->
                TodoItem(
                    todo = todo,
                    onEvent = viewModel::onEvent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.onEvent(TodoListEvent.OnTodoClick(todo))
                        }
                        .padding(16.dp)
                )
            }
        }
    }
}