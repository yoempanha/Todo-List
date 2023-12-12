package com.example.todolist.data.remote.source

import com.example.todolist.data.remote.model.TodoListContentDTO
import com.example.todolist.data.remote.service.FirebaseService
import com.example.todolist.domain.entity.TodoListContentModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class SourceRemoteImpl(
    firestore: FirebaseFirestore
) : FirebaseService {
    private val collectionName = "todo_list_content"
    private val query = firestore.collection(collectionName)

    override suspend fun getTodoList(filter: String): List<TodoListContentDTO> {
        val data = query.orderBy("timestamp", Query.Direction.ASCENDING).get().await().map {
            TodoListContentDTO(
                description = it.data["description"]?.toString().orEmpty(),
                isCompleted = it.data["isCompleted"]?.toString()?.toBoolean() ?: false,
                timestamp = it.data["timestamp"]?.toString().orEmpty(),
                itemHashCode = it.data["itemHashCode"]?.toString()?.toIntOrNull() ?: 0,
                users = it.data["users"] as? List<String> ?: emptyList(),
                referenceId = it.id
            )
        }
        return if (filter.isEmpty()) {
            data
        } else data.filter { it.description.contains(filter) }
    }

    override suspend fun deleteTodoListContent(todoListContent: TodoListContentModel) {
        val referenceId = todoListContent.referenceId ?: return
        query.document(referenceId).delete().await()
    }

    override suspend fun upsertTodoListContent(todoListContent: TodoListContentModel) {
        val content = hashMapOf(
            "description" to todoListContent.description,
            "isCompleted" to todoListContent.isCompleted,
            "timestamp" to todoListContent.timestamp,
            "itemHashCode" to todoListContent.itemHashCode,
            "users" to todoListContent.users
        )
        val referenceId = todoListContent.referenceId.orEmpty()
        if (referenceId.isNotEmpty()) {
            val documentSnapshot = query.document(referenceId)
                .get()
                .await()
            if (documentSnapshot.exists()) {
                query.document(referenceId).update(content).await()
            } else {
                val id = query.add(content).await().id
                content["referenceId"] = id
                query.document(id).update(content).await()
            }
        } else {
            val id = query.add(content).await().id
            content["referenceId"] = id
            query.document(id).update(content).await()
        }
    }
}