package com.example.todolist.domain.repository

import com.example.todolist.domain.entity.TodoListContentModel
import com.google.firebase.firestore.Query

interface TodoListRepository {
    suspend fun upsertTodoListContent(todoListContentModel: TodoListContentModel): TodoListContentModel

    suspend fun deleteTodoListContent(todoListContentModel: TodoListContentModel)

    suspend fun getTodoList(filter: String): List<TodoListContentModel>
}