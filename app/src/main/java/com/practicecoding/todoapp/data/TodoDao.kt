package com.practicecoding.todoapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Upsert
    suspend fun upsertTodo(todo: Todo)

    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Query("SELECT * FROM Todo WHERE id =:id")
    suspend fun getTodoById(id: Int):Todo?

    @Query("SELECT * FROM Todo")
    fun getTodos(): Flow<List<Todo>>
}