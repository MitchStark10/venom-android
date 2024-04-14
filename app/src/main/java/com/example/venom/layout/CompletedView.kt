package com.example.venom.layout

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.venom.classes.GroupBy
import com.example.venom.classes.RefreshCounter
import com.example.venom.classes.SelectedView
import com.example.venom.classes.Task
import com.example.venom.classes.Views
import com.example.venom.components.PageWithGroupedTasks
import com.example.venom.services.RetrofitBuilder
import com.example.venom.services.TaskService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun CompletedView() {

    val tasks = remember {
        mutableStateListOf<Task>()
    }
    var isFetchingTaskData by remember { mutableStateOf(true) }
    val taskService = RetrofitBuilder.getRetrofit().create(TaskService::class.java)

    LaunchedEffect(SelectedView.selectedView, RefreshCounter.refreshListCount) {
        if (SelectedView.selectedView === Views.COMPLETED) {
            taskService.getCompletedTasks().enqueue(object : Callback<ArrayList<Task>> {
                override fun onFailure(call: Call<ArrayList<Task>>, t: Throwable) {
                    isFetchingTaskData = false
                }

                override fun onResponse(
                    call: Call<ArrayList<Task>>,
                    response: Response<ArrayList<Task>>
                ) {
                    isFetchingTaskData = false
                    tasks.clear()
                    response.body()?.let { tasks.addAll(it) }
                }
            })
        }
    }

    if (isFetchingTaskData) {
        CircularProgressIndicator()
    } else {
        PageWithGroupedTasks(
            tasks = ArrayList(tasks.toList()),
            groupBy = GroupBy.LIST,
            showDeleteButton = tasks.size > 0
        )
    }
}