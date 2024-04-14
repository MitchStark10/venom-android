package com.example.venom.utils

import com.example.venom.classes.SelectedView
import com.example.venom.classes.Views

fun getTitleText(): String {
    return when (SelectedView.selectedView) {
        Views.TODAY -> "Today"
        Views.COMPLETED -> "Completed"
        Views.UPCOMING -> "Upcoming"
        Views.LIST -> SelectedView.selectedList?.listName ?: "Venom"
        else -> "Venom"
    }
}