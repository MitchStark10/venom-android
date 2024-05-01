package com.example.venom.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.venom.ListModal
import com.example.venom.TaskModal
import com.example.venom.classes.Modal
import com.example.venom.classes.SelectedView
import com.example.venom.classes.Views

@Composable
fun LayoutRouter() {
    Column(
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp)
    ) {
        when (SelectedView.selectedView) {
            Views.TODAY -> TodayView()
            Views.UPCOMING -> UpcomingView()
            Views.COMPLETED -> CompletedView()
            Views.LIST -> ListView(list = SelectedView.selectedList!!)
        }

        when (SelectedView.openModal) {
            Modal.TASK_MODAL -> TaskModal()
            Modal.LIST_MODAL -> ListModal()
            else -> Unit
        }
    }
}