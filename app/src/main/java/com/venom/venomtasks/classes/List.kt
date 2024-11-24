package com.venom.venomtasks.classes

data class List(
    val id: Int,
    val order: Int,
    val listName: String,
    val tasks: ArrayList<Task>,
    val isStandupList: Boolean
)

data class ListCreationRequestBody(
    val listName: String
)

data class ReorderListsBody(
    val lists: ArrayList<List>
)