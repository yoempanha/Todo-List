package com.example.todolist.domain.usecase

import com.example.todolist.domain.base.BaseResult
import com.example.todolist.domain.base.BaseUseCase
import com.example.todolist.domain.entity.TodoListContentModel
import com.example.todolist.domain.repository.TodoListRepository


class GetTodoListUseCase(
    private val todoListRepository: TodoListRepository
) : BaseUseCase<List<TodoListContentModel>, String>() {
    override suspend fun execute(param: String): BaseResult<List<TodoListContentModel>> {
        return executeAsync {
            todoListRepository.getTodoList(param)
        }
    }
}