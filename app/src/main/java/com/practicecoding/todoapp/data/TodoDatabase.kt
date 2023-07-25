package com.practicecoding.todoapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Todo::class],
    version = 2
)
abstract class TodoDatabase:RoomDatabase() {
    abstract val dao: TodoDao
}