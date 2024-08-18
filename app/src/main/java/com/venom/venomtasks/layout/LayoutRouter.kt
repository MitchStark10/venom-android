package com.venom.venomtasks.layout

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.venom.venomtasks.modals.ListModal
import com.venom.venomtasks.modals.TaskModal
import com.venom.venomtasks.classes.Modal
import com.venom.venomtasks.classes.GlobalState
import com.venom.venomtasks.classes.Views
import com.venom.venomtasks.modals.TagModal

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LayoutRouter() {
    fun onFabClick() {
        if (GlobalState.selectedView == Views.TAGS) {
            GlobalState.openModal = Modal.TAG_MODAL
        } else {
            GlobalState.openModal = Modal.TASK_MODAL
        }
    }
    Scaffold ( floatingActionButton = {
        FloatingActionButton(onClick = { onFabClick() }) {
            Icon(Icons.Filled.Add, "Add New")
        }
    }) {
        Box {
            Column(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp)
            ) {
                when (GlobalState.selectedView) {
                    Views.TODAY -> TodayView()
                    Views.UPCOMING -> UpcomingView()
                    Views.COMPLETED -> CompletedView()
                    Views.LIST -> ListView(list = GlobalState.selectedList!!)
                    Views.TAGS -> TagView()
                }

                when (GlobalState.openModal) {
                    Modal.TASK_MODAL -> TaskModal()
                    Modal.LIST_MODAL -> ListModal()
                    Modal.TAG_MODAL -> TagModal()
                    else -> Unit
                }
            }
        }

    }
}