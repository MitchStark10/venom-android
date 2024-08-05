package com.venom.venomtasks.classes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class Modal {
    TASK_MODAL,
    LIST_MODAL,
    NONE
}

enum class Views {
    TODAY,
    UPCOMING,
    COMPLETED,
    LIST
}

class SelectedView {
    companion object {
        var selectedView by mutableStateOf(Views.TODAY)
        var selectedList: List? by mutableStateOf(null)
        var openModal: Modal by mutableStateOf(Modal.NONE)
        var selectedTask: Task? by mutableStateOf(null)
    }
}