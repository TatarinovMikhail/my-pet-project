package test.model

data class Task(
    val id: Int,
    val name: String,
    val isDeleted: Boolean,
    val createdAt: String,
    val updatedAt: String
)

data class Tasks(
    val tasks: List<Task>
)
