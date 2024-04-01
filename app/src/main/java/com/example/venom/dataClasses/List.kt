package com.example.venom.dataClasses

data class List(
    val id: Int,
    val order: Int,
    val listName: String,
    val tasks: ArrayList<Task>
)
