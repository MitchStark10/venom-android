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
fun TodayView() {

    var tasks = remember {
        mutableStateListOf<Task>()
    }
    var isProcessing by remember { mutableStateOf(true) }

    LaunchedEffect(SelectedView.selectedView, RefreshCounter.refreshTodayCount) {
        if (SelectedView.selectedView === Views.TODAY) {
            val listService = RetrofitBuilder.getRetrofit().create(TaskService::class.java)
            listService.getTodaysTasks().enqueue(object : Callback<ArrayList<Task>> {
                override fun onFailure(call: Call<ArrayList<Task>>, t: Throwable) {
                    isProcessing = false
                }

                override fun onResponse(
                    call: Call<ArrayList<Task>>,
                    response: Response<ArrayList<Task>>
                ) {

                    isProcessing = false
                    tasks.clear()
                    response.body()?.let { tasks.addAll(it) }
                }
            })
        }
    }

    if (isProcessing) {
        CircularProgressIndicator()
    } else {
        PageWithGroupedTasks(
            title = "Today",
            tasks = ArrayList(tasks.toList()),
            groupBy = GroupBy.LIST
        )
    }
}