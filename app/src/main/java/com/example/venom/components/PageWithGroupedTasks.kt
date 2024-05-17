package com.example.venom.components

import android.annotation.SuppressLint
import android.text.format.DateUtils
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.venom.classes.GroupBy
import com.example.venom.classes.ListColumnItem
import com.example.venom.classes.RefreshCounter
import com.example.venom.classes.Task
import com.example.venom.services.RetrofitBuilder
import com.example.venom.services.TaskService
import com.example.venom.utils.getDateFromDateString
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("SimpleDateFormat")
@Composable
fun PageWithGroupedTasks(
    tasks: ArrayList<Task>,
    groupBy: GroupBy,
    showDeleteButton: Boolean = false,
    showListNameInTask: Boolean = false,
) {
    val toastContext = LocalContext.current
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState =
        rememberReorderableLazyListState(
            lazyListState = lazyListState
        ) { from, to ->
            println("from: " + from)
            println("to: " + to)
        }
    var isProcessingDeleteTasks by remember {
        mutableStateOf(false)
    }
    val listColumnItems = remember {
        mutableStateListOf<ListColumnItem>()
    }

    LaunchedEffect(key1 = tasks) {
        val groupedTasks = tasks.sortedBy { it.dueDate }
            .groupBy { if (groupBy == GroupBy.DATE) it.dueDate else it.list.listName }

        val sortedGroups = groupedTasks.entries.filter { !it.key.isNullOrEmpty() }.toMutableList()
        val noDueDateGroup = groupedTasks.entries.find { it.key.isNullOrEmpty() }
        if (noDueDateGroup != null) {
            sortedGroups.add(noDueDateGroup)
        }
        for (group in sortedGroups) {
            var groupText = ""

            if (groupBy === GroupBy.DATE) {
                if (group.key.isNullOrEmpty()) {
                    groupText = "No Due Date"
                } else {
                    val groupDate = getDateFromDateString(
                        group.key,
                        "yyyy-MM-dd",
                        TimeZone.getDefault().id
                    )

                    groupText = DateUtils.getRelativeTimeSpanString(
                        groupDate!!.time,
                        Date().time,
                        DateUtils.DAY_IN_MILLIS,
                    ).toString() + " (" + SimpleDateFormat("EEEE, MM/dd").format(groupDate) + ")"
                }
            } else if (groupBy == GroupBy.LIST && !group.key.isNullOrEmpty()) {
                groupText = group.key!!
            }

            listColumnItems.add(ListColumnItem(title = groupText))

            for (task in group.value) {
                listColumnItems.add(ListColumnItem(task = task))
            }
        }
    }

    Column {
        Spacer(modifier = Modifier.height(10.dp))

        if (listColumnItems.size == 0) {
            Text(text = "No tasks found.")
        }
        LazyColumn(state = lazyListState) {
            items(listColumnItems, key = { it.title ?: it.task!!.id }) { listColumnItem ->
                ReorderableItem(
                    state = reorderableLazyListState,
                    key = { listColumnItem.title ?: listColumnItem.task!!.id },
                    enabled = listColumnItem.title.isNullOrEmpty()
                ) {
                    if (!listColumnItem.title.isNullOrEmpty()) {
                        Column {
                            Text(
                                text = listColumnItem.title,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Divider()
                            Spacer(modifier = Modifier.size(10.dp))
                        }
                    } else if (listColumnItem.task != null) {
                        var isCompleted by remember { mutableStateOf(listColumnItem.task.isCompleted) }
                        fun handleTaskCompletion(updatedIsCompleted: Boolean) {
                            isCompleted = updatedIsCompleted
                            listColumnItem.task.isCompleted = updatedIsCompleted
                            val taskApi =
                                RetrofitBuilder.getRetrofit().create(TaskService::class.java)
                            taskApi.updateTask(listColumnItem.task.id, listColumnItem.task)
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

                        TaskCheckbox(
                            checked = isCompleted,
                            onCheckedChange = { handleTaskCompletion(it) },
                            label = listColumnItem.task.taskName,
                            task = listColumnItem.task,
                            showListName = showListNameInTask
                        )
                    }
                }
            }
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