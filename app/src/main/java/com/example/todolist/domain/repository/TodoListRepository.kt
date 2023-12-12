package com.example.todolist.domain.repository

import com.example.todolist.domain.entity.TodoListContentModel

interface TodoListRepository {
    suspend fun upsertTodoListContent(todoListContentModel: TodoListContentModel)

    suspend fun deleteTodoListContent(todoListContentModel: TodoListContentModel)

    suspend fun getTodoList(filter: String): List<TodoListContentModel>
}