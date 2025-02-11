package com.venom.venomtasks.layout

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.venom.venomtasks.classes.GlobalState
import com.venom.venomtasks.classes.GroupBy
import com.venom.venomtasks.classes.LogTag
import com.venom.venomtasks.classes.RefreshCounter
import com.venom.venomtasks.classes.StandupResponse
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
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Date
import java.util.TimeZone

@Composable
fun StandupView() {

    var isLoading by remember { mutableStateOf(true) }
    val tasks = remember {
        mutableStateListOf<Task>()
    }

    val isMonday = LocalDate.now().dayOfWeek == DayOfWeek.MONDAY

    LaunchedEffect(GlobalState.selectedView, RefreshCounter.refreshListCount) {
        if (GlobalState.selectedView === Views.STANDUP)     {
            val taskService = RetrofitBuilder.getRetrofit().create(TaskService::class.java)
            taskService.getStandupTasks(
                getDateStringFromMillis(
                    Date().time,
                    "yyyy-MM-dd",
                    TimeZone.getDefault().id
                )
            ).enqueue(object : Callback<StandupResponse> {
                override fun onFailure(call: Call<StandupResponse>, t: Throwable) {
                    isLoading = false
                    Log.e(LogTag.STANDUP_VIEW, "Failed to load standup $t")
                }

                override fun onResponse(
                    call: Call<StandupResponse>,
                    response: Response<StandupResponse>
                ) {
                    isLoading = false
                    Log.i(LogTag.STANDUP_VIEW, "standup response ${response.body()}")

                    tasks.clear()

                    response.body()?.yesterday?.map(fun(task): Task {
                        task.type = if (GlobalState.settingsResponse?.dailyReportIgnoreWeekends == true && isMonday) "Friday" else "Yesterday"
                        return task
                    }).let {
                        if (it != null) {
                            tasks.addAll(it)
                        }
                    }

                    response.body()?.today?.map(fun(task): Task {
                        task.type = "Today"
                        return task
                    }).let {
                        if (it != null) {
                            tasks.addAll(it)
                        }
                    }

                    response.body()?.blocked?.map(fun(task): Task {
                        task.type = "Blocked"
                        return task
                    }).let {
                        if (it != null) {
                            tasks.addAll(it)
                        }
                    }
                }
            })
        }
    }

    if (isLoading) {
        CenteredLoader()
    } else {
        PageWithGroupedTasks(tasks = ArrayList(tasks.toList()), groupBy = GroupBy.TYPE, showDeleteButton = false, showListNameInTask = true, enableReorder = false)
    }
}