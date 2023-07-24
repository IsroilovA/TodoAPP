package com.practicecoding.todoapp.di

import android.app.Application
import androidx.room.Room
import com.practicecoding.todoapp.data.TodoDatabase
import com.practicecoding.todoapp.data.TodoRepository
import com.practicecoding.todoapp.data.TodoRespositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object AppModule {
    @Provides
    @Singleton
    fun provideTodoDatabase(app: Application): TodoDatabase{
        return Room.databaseBuilder(
            app,
            TodoDatabase::class.java,
            "todo_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTodoRepository(db: TodoDatabase): TodoRepository {
        return TodoRespositoryImpl(db.dao)
    }
}