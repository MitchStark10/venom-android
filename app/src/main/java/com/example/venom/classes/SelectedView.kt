package com.example.venom.classes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class Modal {
    NEW_TASK_MODAL,
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
    }
}