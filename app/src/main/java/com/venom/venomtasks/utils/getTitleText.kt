package com.venom.venomtasks.utils

import com.venom.venomtasks.classes.SelectedView
import com.venom.venomtasks.classes.Views

fun getTitleText(): String {
    return when (SelectedView.selectedView) {
        Views.TODAY -> "Today"
        Views.COMPLETED -> "Completed"
        Views.UPCOMING -> "Upcoming"
        Views.LIST -> SelectedView.selectedList?.listName ?: "Venom"
        else -> "Venom"
    }
}