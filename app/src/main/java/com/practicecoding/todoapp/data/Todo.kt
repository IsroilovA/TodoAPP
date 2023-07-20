package com.practicecoding.todoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//database table
@Entity
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val isDone: Boolean
)
