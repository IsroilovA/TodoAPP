package com.practicecoding.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.practicecoding.todoapp.add_edit_todo.AddEditTodoScreen
import com.practicecoding.todoapp.add_edit_todo.AddEditTodoViewModel
import com.practicecoding.todoapp.data.TodoDatabase
import com.practicecoding.todoapp.todo_list.TodoListScreen
import com.practicecoding.todoapp.todo_list.TodoListViewModel
import com.practicecoding.todoapp.ui.theme.ToDoAppTheme

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            "contacts.db"
        ).build()
    }
    private val viewModelList by viewModels<TodoListViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TodoListViewModel(db.dao) as T
                }
            }
        }
    )

    private val viewModelAddEdit by viewModels<AddEditTodoViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AddEditTodoViewModel(db.dao) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoAppTheme {
                val stateAddEditTodoScreen by viewModelAddEdit.state.collectAsState()
                val stateTodoListScreen by viewModelList.todoState.collectAsState()
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Routes.TodoListScreen.route
                ) {
                    composable(route = Routes.TodoListScreen.route) {
                        TodoListScreen(
                            state = stateTodoListScreen,
                            onNavigation = { navController.navigate(Routes.AddEditScreen.route) },
                            onEvent = viewModelList::onEvent)
                    }
                    composable(route = Routes.AddEditScreen.route) {
                        AddEditTodoScreen(
                            onEvent = viewModelAddEdit::onEvent,
                            PopBackStack = { navController.popBackStack() },
                            state = stateAddEditTodoScreen)
                    }
                }
            }
        }
    }
}
