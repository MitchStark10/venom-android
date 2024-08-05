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
import com.venom.venomtasks.classes.SelectedView
import com.venom.venomtasks.classes.Task
import com.venom.venomtasks.classes.Views
import com.venom.venomtasks.components.CenteredLoader
import com.venom.venomtasks.components.PageWithGroupedTasks
import com.venom.venomtasks.services.RetrofitBuilder
import com.venom.venomtasks.services.TaskService
import com.venom.venomtasks.utils.getDateStringFromMillis
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import java.util.TimeZone

@Composable
fun TodayView() {

    var tasks = remember {
        mutableStateListOf<Task>()
    }
    var isProcessing by remember { mutableStateOf(true) }

    LaunchedEffect(SelectedView.selectedView, RefreshCounter.refreshListCount) {
        if (SelectedView.selectedView === Views.TODAY) {
            val listService = RetrofitBuilder.getRetrofit().create(TaskService::class.java)
            listService.getTodaysTasks(
                getDateStringFromMillis(
                    Date().time,
                    "yyyy-MM-dd",
                    TimeZone.getDefault().id
                )
            )
                .enqueue(object : Callback<ArrayList<Task>> {
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
        CenteredLoader()
    } else {
        PageWithGroupedTasks(
            tasks = ArrayList(tasks.toList()),
            groupBy = GroupBy.LIST
        )
    }
}