package com.venom.venomtasks.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.venom.venomtasks.classes.GroupBy
import com.venom.venomtasks.classes.RefreshCounter
import com.venom.venomtasks.classes.GlobalState
import com.venom.venomtasks.classes.Task
import com.venom.venomtasks.classes.Views
import com.venom.venomtasks.components.CenteredLoader
import com.venom.venomtasks.components.PageWithGroupedTasks
import com.venom.venomtasks.services.RetrofitBuilder
import com.venom.venomtasks.services.TaskService
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

    LaunchedEffect(GlobalState.selectedView, RefreshCounter.refreshListCount) {
        if (GlobalState.selectedView === Views.COMPLETED) {
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
        CenteredLoader()
    } else {
        PageWithGroupedTasks(
            tasks = ArrayList(tasks.toList()),
            groupBy = GroupBy.DATE,
            showDeleteButton = tasks.size > 0
        )
    }
}