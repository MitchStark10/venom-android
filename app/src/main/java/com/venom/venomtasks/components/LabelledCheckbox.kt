package com.venom.venomtasks.components

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.venomtasks.classes.Modal
import com.venom.venomtasks.classes.GlobalState
import com.venom.venomtasks.classes.RefreshCounter
import com.venom.venomtasks.classes.Task
import com.venom.venomtasks.services.RetrofitBuilder
import com.venom.venomtasks.services.TaskService
import com.venom.venomtasks.utils.getDateStringFromMillis
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import java.util.TimeZone

@Composable
fun TaskCheckbox(
    label: String,
    modifier: Modifier = Modifier,
    task: Task,
    showListName: Boolean = false
) {

    val toastContext = LocalContext.current
    var isCompleted by remember { mutableStateOf(task.isCompleted) }

    fun handleTaskCompletion(updatedIsCompleted: Boolean) {
        isCompleted = updatedIsCompleted
        task.isCompleted = updatedIsCompleted
        task.dateCompleted =
            getDateStringFromMillis(
                Date().time,
                "yyyy-MM-dd",
                TimeZone.getDefault().id
            )
        val taskApi =
            RetrofitBuilder.getRetrofit().create(TaskService::class.java)
        taskApi.updateTask(task.id, task)
            .enqueue(object : Callback<Unit> {
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Toast.makeText(
                        toastContext,
                        "Unable to mark task as completed",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    RefreshCounter.refreshListCount++
                    isCompleted = false
                }
            })
    }

    fun editTask() {
        GlobalState.selectedTask = task;
        GlobalState.openModal = Modal.TASK_MODAL
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .padding(4.dp)
            .wrapContentWidth()
            .wrapContentSize(),
    ) {
        if (task.recurringSchedule != null && !isCompleted) {
            IconButton(onClick = {
                handleTaskCompletion(true)
            }) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Mark task as completed"
                )
            }
        } else {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { handleTaskCompletion(it) },
            )
        }


        Spacer(Modifier.size(6.dp))

        Column {
            Text(
                text = label,
                overflow = TextOverflow.Visible,
                modifier = Modifier.clickable {
                    editTask()
                }
            )

            if (showListName) {
                Text(
                    text = task.list!!.listName,
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                task.taskTag.forEach { taskTag ->
                    TagPill(tag = taskTag.tag, onClick = { editTask() })
                }
            }
        }

    }
}