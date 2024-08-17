package com.venom.venomtasks.layout

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
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
import com.venom.venomtasks.ListModal
import com.venom.venomtasks.TaskModal
import com.venom.venomtasks.classes.Modal
import com.venom.venomtasks.classes.GlobalState
import com.venom.venomtasks.classes.Views

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LayoutRouter() {
    Scaffold ( floatingActionButton = {
        FloatingActionButton(onClick = { GlobalState.openModal = Modal.TASK_MODAL }) {
            Icon(Icons.Filled.Add, "Add New Task")
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
                }

                when (GlobalState.openModal) {
                    Modal.TASK_MODAL -> TaskModal()
                    Modal.LIST_MODAL -> ListModal()
                    else -> Unit
                }
            }
        }

    }
}