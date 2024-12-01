package com.venom.venomtasks.classes

data class List(
    val id: Int,
    val order: Int,
    var listName: String,
    val tasks: ArrayList<Task>,
    var isStandupList: Boolean
)

data class CreateOrUpdateListRequestBody(
    val listName: String
)

data class ReorderListsBody(
    val lists: ArrayList<List>
)