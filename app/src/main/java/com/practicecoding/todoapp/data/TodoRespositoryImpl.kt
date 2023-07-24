package com.practicecoding.todoapp.data

import kotlinx.coroutines.flow.Flow

class TodoRespositoryImpl(
    private val dao: TodoDao
): TodoRepository {
    override suspend fun upsertTodo(todo: Todo) {
        dao.upsertTodo(todo)
    }

    override suspend fun deleteTodo(todo: Todo) {
        dao.deleteTodo(todo)
    }

    override suspend fun getTodoById(id: Int): Todo? {
        return dao.getTodoById(id)
    }

    override fun getTodos(): Flow<List<Todo>> {
        return dao.getTodos()
    }
}