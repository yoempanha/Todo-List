package com.example.todolist.data.source

import com.example.todolist.data.remote.service.FirebaseService
import com.example.todolist.domain.entity.TodoListContentModel
import com.example.todolist.domain.mapper.toTodoListContentModel
import com.example.todolist.domain.repository.TodoListRepository
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TodoListSourceDataImpl(
    private val service: FirebaseService
) : TodoListRepository {

    override suspend fun upsertTodoListContent(todoListContentModel: TodoListContentModel): TodoListContentModel {
        return withContext(Dispatchers.IO) {
            service.upsertTodoListContent(todoListContentModel)
        }
    }

    override suspend fun deleteTodoListContent(todoListContentModel: TodoListContentModel) {
        return withContext(Dispatchers.IO) {
            service.deleteTodoListContent(todoListContentModel)
        }
    }

    override suspend fun getTodoList(filter: String): List<TodoListContentModel> {
        return withContext(Dispatchers.IO) {
            try {
                service.getTodoList(filter).map { it.toTodoListContentModel() }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}