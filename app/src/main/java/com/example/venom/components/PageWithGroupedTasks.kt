package com.example.venom.components

import android.text.format.DateUtils
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
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
import com.example.venom.classes.GroupBy
import com.example.venom.classes.RefreshCounter
import com.example.venom.classes.Task
import com.example.venom.services.RetrofitBuilder
import com.example.venom.services.TaskService
import com.example.venom.utils.getDateFromDateString
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import java.util.TimeZone

@Composable
fun PageWithGroupedTasks(
    title: String,
    tasks: ArrayList<Task>,
    groupBy: GroupBy,
    showDeleteButton: Boolean = false
) {
    val toastContext = LocalContext.current
    val groupedTasks = tasks.sortedBy { it.dueDate }
        .groupBy { if (groupBy == GroupBy.DATE) it.dueDate else it.list.listName }

    val sortedGroups = groupedTasks.entries.filter { !it.key.isNullOrEmpty() }.toMutableList()
    val noDueDateGroup = groupedTasks.entries.find { it.key.isNullOrEmpty() }

    if (noDueDateGroup != null) {
        sortedGroups.add(noDueDateGroup)
    }
    var isProcessingDeleteTasks by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = androidx.compose.ui.Modifier
            .verticalScroll(rememberScrollState())
    ) {
        Text(title, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.size(10.dp))
        Divider()
        Spacer(modifier = Modifier.size(20.dp))
        for (group in sortedGroups) {
            var groupText = ""

            if (groupBy === GroupBy.DATE) {
                groupText = if (group.key.isNullOrEmpty()) {
                    "No Due Date"
                } else {
                    DateUtils.getRelativeTimeSpanString(
                        getDateFromDateString(
                            group.key,
                            "yyyy-MM-dd",
                            TimeZone.getDefault().id
                        )!!.time,
                        Date().time,
                        DateUtils.DAY_IN_MILLIS,
                    ).toString()
                }
            } else if (groupBy == GroupBy.LIST && !group.key.isNullOrEmpty()) {
                groupText = group.key!!
            }


            Text(text = groupText, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Divider()
            Spacer(modifier = Modifier.size(10.dp))

            for (task in group.value) {
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
                            isCompleted = false
                        }
                    })
                }

                TaskCheckbox(
                    checked = isCompleted,
                    onCheckedChange = { handleTaskCompletion(it) },
                    label = task.taskName,
                    task = task
                )
                Spacer(modifier = Modifier.size(10.dp))
            }

            Spacer(modifier = Modifier.size(20.dp))
        }

        if (showDeleteButton) {
            Button(
                onClick = {
                    isProcessingDeleteTasks = true
                    val taskService = RetrofitBuilder.getRetrofit().create(TaskService::class.java)
                    taskService.deleteCompletedTasks().enqueue(object : Callback<Unit> {
                        override fun onFailure(call: Call<Unit>, t: Throwable) {
                            isProcessingDeleteTasks = false
                        }

                        override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                            RefreshCounter.refreshListCount++;
                            isProcessingDeleteTasks = false
                        }
                    })
                },
                enabled = !isProcessingDeleteTasks
            ) {
                Text(text = if (isProcessingDeleteTasks) "Processing..." else "Delete All Tasks")
            }
        }
    }
}