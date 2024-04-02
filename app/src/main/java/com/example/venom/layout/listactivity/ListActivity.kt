package com.example.venom.layout.listactivity

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.venom.classes.List
import com.example.venom.classes.RefreshCounter
import com.example.venom.components.LabelledCheckBox
import com.example.venom.services.RetrofitBuilder
import com.example.venom.services.TaskService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ListActivity(list: List) {

    val toastContext = LocalContext.current

    Column {
        Text(list.listName, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.size(10.dp))
        Divider()
        Spacer(modifier = Modifier.size(20.dp))
        for (task in list.tasks) {
            fun handleTaskCompletion(isCompleted: Boolean) {
                task.isCompleted = isCompleted
                val taskApi = RetrofitBuilder.getRetrofit().create(TaskService::class.java)
                println("Before sending update request")
                taskApi.updateTask(task.id, task).enqueue(object : Callback<Unit> {
                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                        Toast.makeText(
                            toastContext,
                            "Unable to mark task as completed",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                        println("After sending update request, triggering list refresh")
                        RefreshCounter.refreshListCount++
                    }
                })
            }

            LabelledCheckBox(
                checked = task.isCompleted,
                onCheckedChange = { handleTaskCompletion(it) },
                label = task.taskName
            )
            Spacer(modifier = Modifier.size(10.dp))
        }
    }
}