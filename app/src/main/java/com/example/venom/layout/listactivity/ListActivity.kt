package com.example.venom.layout.listactivity

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.venom.classes.List
import com.example.venom.classes.Modal
import com.example.venom.classes.RefreshCounter
import com.example.venom.classes.SelectedView
import com.example.venom.components.LabelledCheckBox
import com.example.venom.services.RetrofitBuilder
import com.example.venom.services.TaskService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ListActivity(list: List) {
    val toastContext = LocalContext.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { SelectedView.openModal = Modal.NEW_TASK_MODAL }) {
                Icon(Icons.Filled.Add, "Add New Task")
            }
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Text(list.listName, fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.size(10.dp))
            Divider()
            Spacer(modifier = Modifier.size(20.dp))
            for (task in list.tasks) {
                var isCompleted by remember { mutableStateOf(task.isCompleted) }
                fun handleTaskCompletion(updatedIsCompleted: Boolean) {
                    isCompleted = updatedIsCompleted
                    task.isCompleted = updatedIsCompleted
                    val taskApi = RetrofitBuilder.getRetrofit().create(TaskService::class.java)
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
                            RefreshCounter.refreshListCount++
                        }
                    })
                }

                LabelledCheckBox(
                    checked = isCompleted,
                    onCheckedChange = { handleTaskCompletion(it) },
                    label = task.taskName
                )
                Spacer(modifier = Modifier.size(10.dp))
            }
        }
    }
}