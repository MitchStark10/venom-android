package com.venom.venomtasks.components

import android.annotation.SuppressLint
import android.os.Build
import android.text.format.DateUtils
import android.util.Log
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.venom.venomtasks.classes.GroupBy
import com.venom.venomtasks.classes.ListColumnItem
import com.venom.venomtasks.classes.LogTag
import com.venom.venomtasks.classes.RefreshCounter
import com.venom.venomtasks.classes.Task
import com.venom.venomtasks.classes.TaskReorderBody
import com.venom.venomtasks.classes.TaskReorderItem
import com.venom.venomtasks.services.RetrofitBuilder
import com.venom.venomtasks.services.TaskService
import com.venom.venomtasks.utils.getDateFromDateString
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
    isListPage: Boolean = false
) {
    val view = LocalView.current
    val toastContext = LocalContext.current
    val lazyListState = rememberLazyListState()

    var isProcessingDeleteTasks by remember {
        mutableStateOf(false)
    }
    val listColumnItems = remember {
        mutableStateListOf<ListColumnItem>()
    }

    val reorderableLazyListState =
        rememberReorderableLazyListState(
            lazyListState = lazyListState
        ) { from, to ->
            listColumnItems.apply {
                add(to.index, removeAt(from.index))

                var neighboringTask: ListColumnItem? = null
                if (to.index > 0) {
                    val neighborItemAbove = get(to.index - 1)
                    if (neighborItemAbove.task != null) {
                        neighboringTask = neighborItemAbove
                    }
                }

                if (neighboringTask == null && to.index < size - 1) {
                    val neighborItemBelow = get(to.index + 1)
                    if (neighborItemBelow.task != null) {
                        neighboringTask = neighborItemBelow
                    }
                }

                val itemToUpdate = get(to.index)
                if (itemToUpdate.task != null) {

                    if (groupBy == GroupBy.LIST) {
                        itemToUpdate.task.listViewOrder = to.index
                    } else {
                        itemToUpdate.task.combinedViewOrder = to.index
                    }

                    if (neighboringTask?.task != null) {
                        itemToUpdate.task.dueDate = neighboringTask.task!!.dueDate
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    view.performHapticFeedback(HapticFeedbackConstants.SEGMENT_FREQUENT_TICK)
                }

            }
        }

    LaunchedEffect(key1 = tasks) {
        listColumnItems.clear()
        val groupedTasks = tasks.sortedBy { it.dueDate }
            .groupBy {
                when (groupBy) {
                    GroupBy.DATE -> it.dueDate
                    GroupBy.TYPE -> it.type
                    GroupBy.LIST -> it.list!!.listName
                }
            }

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
                    ).toString()

                    if (groupText.startsWith("In")) {
                        groupText += " (" + SimpleDateFormat("EEEE, MM/dd").format(groupDate) + ")"
                    }
                }
            } else if ((groupBy == GroupBy.LIST || groupBy == GroupBy.TYPE) && !group.key.isNullOrEmpty()) {
                groupText = group.key!!
            }

            listColumnItems.add(ListColumnItem(title = groupText))

            for (task in group.value.sortedBy { if (groupBy == GroupBy.DATE) it.listViewOrder else it.combinedViewOrder }) {
                listColumnItems.add(ListColumnItem(task = task))
            }
        }
    }

    Column {
        if (listColumnItems.size == 0) {
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = "No tasks found.")
        }
        LazyColumn(state = lazyListState) {
            itemsIndexed(listColumnItems, key = { _, item ->  item.title ?: item.task!!.id }) { index, listColumnItem ->
                if (index == 0) {
                    Spacer(modifier = Modifier.size(10.dp))
                }
                ReorderableItem(
                    state = reorderableLazyListState,
                    key = listColumnItem.title ?: listColumnItem.task!!.id,
                    enabled = listColumnItem.title.isNullOrEmpty()
                ) {
                    Row(
                        modifier = Modifier.longPressDraggableHandle(
                            onDragStarted = {
                                view.performHapticFeedback(HapticFeedbackConstants.GESTURE_START)
                            },
                            onDragStopped = {
                                view.performHapticFeedback(HapticFeedbackConstants.GESTURE_END)
                                var groupIndex = 0
                                val tasksInListColumnItems = ArrayList<TaskReorderItem>()

                                listColumnItems.forEach { listItem ->
                                    if (listItem.task != null) {
                                        tasksInListColumnItems.add(TaskReorderItem(
                                            id = listItem.task.id,
                                            newOrder = groupIndex,
                                            newDueDate = listItem.task.dueDate,
                                            fieldToUpdate = if (groupBy == GroupBy.DATE) "listViewOrder" else "combinedViewOrder"
                                        ))
                                        groupIndex++
                                    } else {
                                        groupIndex = 0
                                    }
                                }

                                val taskReorderBody = TaskReorderBody(tasksInListColumnItems)

                                val taskService =
                                    RetrofitBuilder.getRetrofit().create(TaskService::class.java)
                                taskService.reorderTasks(taskReorderBody)
                                    .enqueue(object : Callback<Unit> {
                                        override fun onResponse(
                                            call: Call<Unit>,
                                            response: Response<Unit>
                                        ) {
                                            if (response.isSuccessful) {
                                                RefreshCounter.refreshListCount++
                                                return
                                            }
                                            Log.e(LogTag.PAGE_WITH_GROUPED_TASKS, "Unsuccessful reorder response: " + response.body())
                                            Toast.makeText(
                                                toastContext,
                                                "Unable to save reordered items.",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }

                                        override fun onFailure(call: Call<Unit>, t: Throwable) {
                                            Log.e(LogTag.PAGE_WITH_GROUPED_TASKS, "Failed API call to reorder", t)
                                            Toast.makeText(
                                                toastContext,
                                                "Unable to save reordered items.",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }
                                    })
                            },
                            enabled = listColumnItem.title.isNullOrEmpty()
                        )
                    ) {
                        if (!listColumnItem.title.isNullOrEmpty()) {
                            Column {
                                if (index != 0) {
                                    Spacer(modifier = Modifier.size(20.dp))
                                }
                                SectionHeader(text = listColumnItem.title)
                                Divider()
                                Spacer(modifier = Modifier.size(10.dp))
                            }
                        } else if (listColumnItem.task != null) {
                            TaskCheckbox(
                                label = listColumnItem.task.taskName,
                                task = listColumnItem.task,
                                showListName = showListNameInTask
                            )
                        }
                    }

                }

                if (index == listColumnItems.size - 1 && showDeleteButton) {
                    Button(
                        onClick = {
                            isProcessingDeleteTasks = true
                            val taskService = RetrofitBuilder.getRetrofit().create(TaskService::class.java)
                            taskService.deleteCompletedTasks().enqueue(object : Callback<Unit> {
                                override fun onFailure(call: Call<Unit>, t: Throwable) {
                                    isProcessingDeleteTasks = false
                                }

                                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                                    RefreshCounter.refreshListCount++
                                    isProcessingDeleteTasks = false
                                }
                            })
                        },
                        enabled = !isProcessingDeleteTasks
                    ) {
                        Text(text = if (isProcessingDeleteTasks) "Processing..." else "Delete All Tasks")
                    }
                }

                if (index == listColumnItems.size - 1) {
                    Spacer(modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}