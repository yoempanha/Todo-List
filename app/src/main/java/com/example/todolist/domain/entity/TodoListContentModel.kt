package com.example.todolist.domain.entity

data class TodoListContentModel(
    val referenceId: String? = null,
    val description: String = "",
    val isCompleted: Boolean = false,
    val timestamp: String = "",
    val itemHashCode: Int = 0,
    val users: List<String> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        return if (other is TodoListContentModel) {
            other.referenceId == referenceId
        } else super.equals(other)
    }

    override fun hashCode(): Int {
        var result = referenceId?.hashCode() ?: 0
        result = 31 * result + description.hashCode()
        result = 31 * result + isCompleted.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + itemHashCode.hashCode()
        result = 31 * result + users.hashCode()
        return result
    }
}
