package com.example.todolist.domain.mapper

import com.example.todolist.data.remote.model.TodoListContentDTO
import com.example.todolist.domain.entity.TodoListContentModel
import com.google.firebase.firestore.QueryDocumentSnapshot

fun TodoListContentDTO.toTodoListContentModel(): TodoListContentModel {
    return TodoListContentModel(
        description = description,
        isCompleted = isCompleted,
        timestamp = timestamp,
        itemHashCode = itemHashCode,
        users = users,
        referenceId = referenceId
    )
}

fun QueryDocumentSnapshot.toContentModel(): TodoListContentModel {
    return TodoListContentModel(
        description = data["description"].toString(),
        isCompleted = data["isCompleted"].toString().toBoolean(),
        timestamp = data["timestamp"].toString(),
        itemHashCode = data["itemHashCode"].toString().toInt(),
        users = data["users"] as? List<String> ?: emptyList(),
        referenceId = data["referenceId"]?.toString()
    )
}