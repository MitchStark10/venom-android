package com.example.venom.dataClasses

data class Task(
    val id: Int,
    val taskName: String,
    val dueDate: String?,
    val listViewOrder: Int?,
    val timeViewOrder: Int?,
    val isCompleted: Boolean,
    val list: List
)
