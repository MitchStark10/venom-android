package com.venom.venom.utils

import com.venom.venom.classes.SelectedView
import com.venom.venom.classes.Views

fun getTitleText(): String {
    return when (SelectedView.selectedView) {
        Views.TODAY -> "Today"
        Views.COMPLETED -> "Completed"
        Views.UPCOMING -> "Upcoming"
        Views.LIST -> SelectedView.selectedList?.listName ?: "Venom"
        else -> "Venom"
    }
}