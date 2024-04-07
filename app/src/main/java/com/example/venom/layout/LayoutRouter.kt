package com.example.venom.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.venom.TaskModal
import com.example.venom.classes.Modal
import com.example.venom.classes.SelectedView
import com.example.venom.classes.Views

@Composable
fun LayoutRouter() {
    Box(modifier = Modifier.padding(10.dp)) {
        when (SelectedView.selectedView) {
            Views.TODAY -> TodayView()
            Views.UPCOMING -> Text("Upcoming")
            Views.COMPLETED -> CompletedView()
            Views.LIST -> ListView(list = SelectedView.selectedList!!)
        }

        when (SelectedView.openModal) {
            Modal.TASK_MODAL -> TaskModal()
            Modal.NONE -> Unit
        }
    }
}