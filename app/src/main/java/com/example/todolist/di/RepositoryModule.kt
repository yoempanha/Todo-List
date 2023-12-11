package com.example.todolist.di

import com.example.todolist.data.remote.service.FirebaseService
import com.example.todolist.data.source.TodoListSourceDataImpl
import com.example.todolist.domain.repository.TodoListRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {
    @Provides
    fun provideTodoListRepository(service: FirebaseService): TodoListRepository {
        return TodoListSourceDataImpl(service)
    }
}