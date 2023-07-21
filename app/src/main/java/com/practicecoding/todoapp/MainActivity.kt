package com.practicecoding.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.practicecoding.todoapp.util.Routes

class MainActivity : ComponentActivity() {
    //instance of database
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            "contacts.db"
        ).build()
    }
    //instance of TodoListViewModel
    private val viewModelTodoList by viewModels<TodoListViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TodoListViewModel(db.dao) as T
                }
            }
        }
    )
    //instance of AddEditTodoViewModel
    private val viewModelAddEditTodo by viewModels<AddEditTodoViewModel>(
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
                //states of screens
                val stateAddEditTodoScreen by viewModelAddEditTodo.state.collectAsState()
                val stateTodoListScreen by viewModelTodoList.todoState.collectAsState()
                //navController instance
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Routes.TodoListScreen.route
                ) {
                    composable(route = Routes.TodoListScreen.route) {
                        TodoListScreen(
                            state = stateTodoListScreen,
                            viewModel = viewModelTodoList,
                            onNavigate = { navController.navigate(it.route) }
                        )
                    }
                    composable(route = Routes.AddEditTodoScreen.route) {
                        AddEditTodoScreen(
                            state = stateAddEditTodoScreen,
                            viewModel = viewModelAddEditTodo,
                            onPopBackStack = {navController.popBackStack()}
                        )
                    }
                }
            }
        }
    }
}
