package com.practicecoding.todoapp.todo_list

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.practicecoding.todoapp.TodoState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    state: TodoState,
    onEvent: (TodoListEvent) -> Unit
) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(TodoListEvent.AddTodo)
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
            items(state.todos){todo->
                TodoItem(
                    todo = todo,
                    onEvent = onEvent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}