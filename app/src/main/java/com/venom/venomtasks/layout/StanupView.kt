package com.venom.venomtasks.layout

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.venom.venomtasks.classes.GlobalState
import com.venom.venomtasks.classes.RefreshCounter
import com.venom.venomtasks.classes.StandupResponse
import com.venom.venomtasks.classes.Task
import com.venom.venomtasks.classes.Views
import com.venom.venomtasks.components.CenteredLoader
import com.venom.venomtasks.services.RetrofitBuilder
import com.venom.venomtasks.services.TaskService
import com.venom.venomtasks.utils.getDateStringFromMillis
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import java.util.TimeZone

@Composable
fun StandupView() {

    var isLoading by remember { mutableStateOf(true) }
    var yesterdayTasks = remember {
        mutableStateListOf<Task>()
    }
    var todayTasks = remember {
        mutableStateListOf<Task>()
    }
    var blockedTasks = remember {
        mutableStateListOf<Task>()
    }

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
                }

                override fun onResponse(
                    call: Call<StandupResponse>,
                    response: Response<StandupResponse>
                ) {
                    isLoading = false
                    response.body()?.yesterday.let {
                        if (it != null) {
                            yesterdayTasks.addAll(it)
                        }
                    }

                    response.body()?.today.let {
                        if (it != null) {
                            todayTasks.addAll(it)
                        }
                    }

                    response.body()?.blocked.let {
                        if (it != null) {
                            blockedTasks.addAll(it)
                        }
                    }
                }
            })
        }
    }

    if (isLoading) {
        CenteredLoader()
    } else {
        Text("Ready to render tasks!")
    }
}