package com.venom.venomtasks.layout

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.venom.venomtasks.classes.GroupBy
import com.venom.venomtasks.classes.List
import com.venom.venomtasks.classes.Modal
import com.venom.venomtasks.classes.SelectedView
import com.venom.venomtasks.components.PageWithGroupedTasks

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListView(list: List) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { SelectedView.openModal = Modal.TASK_MODAL }) {
                Icon(Icons.Filled.Add, "Add New Task")
            }
        }
    ) {
        Box {
            PageWithGroupedTasks(tasks = list.tasks, groupBy = GroupBy.DATE, enableReorder = true)
        }
    }
}