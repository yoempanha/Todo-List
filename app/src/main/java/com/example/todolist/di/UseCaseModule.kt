package com.example.todolist.di

import com.example.todolist.domain.repository.TodoListRepository
import com.example.todolist.domain.usecase.DeleteTodoListContentUseCase
import com.example.todolist.domain.usecase.GetTodoListUseCase
import com.example.todolist.domain.usecase.UpsertTodoListContentUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object UseCaseModule {
    @Provides
    fun providesDeleteTodoListContentUseCase(
        repository: TodoListRepository
    ): DeleteTodoListContentUseCase {
        return DeleteTodoListContentUseCase(repository)
    }

    @Provides
    fun providesUpsertTodoListContentUseCase(
        repository: TodoListRepository
    ): UpsertTodoListContentUseCase {
        return UpsertTodoListContentUseCase(repository)
    }

    @Provides
    fun providesGetTodoListUseCase(
        repository: TodoListRepository
    ): GetTodoListUseCase {
        return GetTodoListUseCase(repository)
    }
}