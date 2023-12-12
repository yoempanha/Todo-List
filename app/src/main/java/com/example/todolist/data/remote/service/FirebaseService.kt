package com.example.todolist.data.remote.service

import com.example.todolist.data.remote.model.TodoListContentDTO
import com.example.todolist.domain.entity.TodoListContentModel

interface FirebaseService {
    suspend fun getTodoList(filter: String): List<TodoListContentDTO>

    suspend fun deleteTodoListContent(todoListContent: TodoListContentModel)

    suspend fun updateTodoListContent(todoListContent: TodoListContentModel)

    suspend fun insertTodoListContent(todoListContent: TodoListContentModel)
}