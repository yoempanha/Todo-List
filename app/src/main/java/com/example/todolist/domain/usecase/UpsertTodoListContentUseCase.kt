package com.example.todolist.domain.usecase

import com.example.todolist.domain.base.BaseResult
import com.example.todolist.domain.base.BaseUseCase
import com.example.todolist.domain.entity.TodoListContentModel
import com.example.todolist.domain.repository.TodoListRepository

class UpsertTodoListContentUseCase(
    private val todoListRepository: TodoListRepository
) : BaseUseCase<TodoListContentModel, TodoListContentModel>() {
    override suspend fun execute(param: TodoListContentModel): BaseResult<TodoListContentModel> {
        return executeAsync {
            todoListRepository.upsertTodoListContent(param)
        }
    }
}