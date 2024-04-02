package com.example.venom.classes

data class Task(
    val id: Int,
    val taskName: String,
    val dueDate: String?,
    val listViewOrder: Int?,
    val timeViewOrder: Int?,
    var isCompleted: Boolean,
    val list: List
)
