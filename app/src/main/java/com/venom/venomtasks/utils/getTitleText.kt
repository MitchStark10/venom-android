package com.venom.venomtasks.utils

import com.venom.venomtasks.classes.GlobalState
import com.venom.venomtasks.classes.Views

fun getTitleText(): String {
    return when (GlobalState.selectedView) {
        Views.TODAY -> "Today"
        Views.COMPLETED -> "Completed"
        Views.UPCOMING -> "Upcoming"
        Views.LIST -> GlobalState.selectedList?.listName ?: "Venom"
        else -> "Venom"
    }
}