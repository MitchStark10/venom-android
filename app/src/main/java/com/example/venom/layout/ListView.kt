package com.example.venom.layout

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
import com.example.venom.classes.GroupBy
import com.example.venom.classes.List
import com.example.venom.classes.Modal
import com.example.venom.classes.SelectedView
import com.example.venom.components.PageWithGroupedTasks

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