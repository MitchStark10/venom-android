package com.example.venom.classes

data class Task(
    val id: Int,
    var taskName: String,
    var dueDate: String?,
    val listViewOrder: Int?,
    val timeViewOrder: Int?,
    var isCompleted: Boolean,
    val list: List
)

data class CreateTaskRequestBody(
    val taskName: String,
    val dueDate: String?,
    val listId: Int
)
