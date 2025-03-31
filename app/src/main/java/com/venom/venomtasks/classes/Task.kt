package com.venom.venomtasks.classes

data class Task(
    val id: Int,
    var taskName: String,
    var dueDate: String?,
    var listViewOrder: Int?,
    var combinedViewOrder: Int?,
    var isCompleted: Boolean,
    val list: List?,
    var listId: Int?,
    val taskTag: ArrayList<TaskTag>,
    var tagIds: ArrayList<Int>,
    var type: String?
)

data class CreateTaskRequestBody(
    val taskName: String,
    val dueDate: String?,
    val listId: Int,
    val tagIds: ArrayList<Int>
)

data class TaskReorderItem(
    val id: Int,
    val newOrder: Int,
    val newDueDate: String?,
    val fieldToUpdate: String
)

data class TaskReorderBody(
    val tasksToUpdate: kotlin.collections.List<TaskReorderItem>
)

data class StandupResponse(
    val yesterday: ArrayList<Task>,
    val today: ArrayList<Task>,
    val blocked: ArrayList<Task>
)