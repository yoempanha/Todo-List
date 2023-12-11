package com.example.todolist.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class TodoListContentDTO(
    val referenceId: String? = null,
    val description: String,
    val isCompleted: Boolean,
    val timestamp: String,
    val itemHashCode: Int,
    val users: List<String>
)
