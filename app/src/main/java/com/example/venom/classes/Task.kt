package com.example.venom.classes

data class Task(
    val id: Int,
    var taskName: String,
    var dueDate: String?,
    var listViewOrder: Int?,
    var timeViewOrder: Int?,
    var isCompleted: Boolean,
    val list: List
)

data class CreateTaskRequestBody(
    val taskName: String,
    val dueDate: String?,
    val listId: Int
)

data class TaskReorderItem(
    val id: Int,
    val fieldToUpdate: String,
    val newOrder: Int,
    val newDueDate: String?
)

data class TaskReorderBody(
    val tasksToUpdate: kotlin.collections.List<TaskReorderItem>
)
