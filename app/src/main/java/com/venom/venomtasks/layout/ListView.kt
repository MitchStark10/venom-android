package com.venom.venomtasks.layout

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.venom.venomtasks.classes.GroupBy
import com.venom.venomtasks.classes.List
import com.venom.venomtasks.components.PageWithGroupedTasks

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListView(list: List) {
    PageWithGroupedTasks(tasks = list.tasks, groupBy = GroupBy.DATE)
}