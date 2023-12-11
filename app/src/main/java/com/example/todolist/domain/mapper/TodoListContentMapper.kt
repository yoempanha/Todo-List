package com.example.todolist.domain.mapper

import com.example.todolist.data.remote.model.TodoListContentDTO
import com.example.todolist.domain.entity.TodoListContentModel

fun TodoListContentDTO.toTodoListContentModel(): TodoListContentModel {
    return TodoListContentModel(
        description = description,
        isCompleted = isCompleted,
        timestamp = timestamp,
        hashCode = itemHashCode,
        users = users,
        referenceId = referenceId
    )
}