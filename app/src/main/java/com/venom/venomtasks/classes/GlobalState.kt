package com.venom.venomtasks.classes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.venom.venomtasks.utils.getSharedPreferences

enum class Modal {
    TASK_MODAL,
    CREATE_LIST_MODAL,
    UPDATE_LIST_MODAL,
    TAG_MODAL,
    DELETE_LIST_MODAL,
    DELETE_TAG_MODAL,
    NONE
}

enum class Views {
    TODAY,
    UPCOMING,
    STANDUP,
    COMPLETED,
    LIST,
    TAGS,
    SETTINGS
}

class GlobalState {
    companion object {
        var selectedView by mutableStateOf(Views.TODAY)
        var selectedList: List? by mutableStateOf(null)
        var openModal: Modal by mutableStateOf(Modal.NONE)
        var selectedTask: Task? by mutableStateOf(null)
        var selectedTag: Tag? by mutableStateOf(null)
        val lists = mutableStateListOf<List>()
        val tags = mutableStateListOf<Tag>()
        var settingsResponse by mutableStateOf<SettingsResponse?>(null)
    }
}