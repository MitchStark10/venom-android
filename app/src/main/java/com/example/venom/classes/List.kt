package com.example.venom.classes

data class List(
    val id: Int,
    val order: Int,
    val listName: String,
    val tasks: ArrayList<Task>
)

data class ListCreationRequestBody(
    val listName: String
)