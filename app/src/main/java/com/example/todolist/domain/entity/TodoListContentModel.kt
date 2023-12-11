package com.example.todolist.domain.entity

data class TodoListContentModel(
    val referenceId: String? = null,
    val description: String,
    var isCompleted: Boolean,
    val timestamp: String,
    val hashCode: Int,
    val users: List<String>
)
