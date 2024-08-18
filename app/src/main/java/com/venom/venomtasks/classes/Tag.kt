package com.venom.venomtasks.classes

data class TaskTag (
    val taskId: Int,
    val tagId: Int,
    val tag: Tag
)

data class TagCreationRequestBody(
    var tagName: String,
    var tagColor: String
)

data class Tag (
    val id: Int,
    var tagName: String,
    var tagColor: String
)