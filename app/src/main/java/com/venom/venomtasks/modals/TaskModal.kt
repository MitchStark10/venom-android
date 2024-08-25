package com.venom.venomtasks.modals

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.venom.venomtasks.classes.CreateTaskRequestBody
import com.venom.venomtasks.classes.Modal
import com.venom.venomtasks.classes.RefreshCounter
import com.venom.venomtasks.classes.GlobalState
import com.venom.venomtasks.components.CustomDatePicker
import com.venom.venomtasks.components.CustomDropdown
import com.venom.venomtasks.components.DropdownOption
import com.venom.venomtasks.components.SectionHeader
import com.venom.venomtasks.services.RetrofitBuilder
import com.venom.venomtasks.services.TaskService
import com.venom.venomtasks.utils.getDateFromDateString
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.TimeZone
import kotlin.collections.ArrayList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskModal() {
    val initialTaskName = GlobalState.selectedTask?.taskName ?: ""
    val initialListId =
        GlobalState.selectedTask?.list?.id ?: GlobalState.selectedList?.id ?: GlobalState.lists[0].id
    var initialTaskDate: String? = null;
    val context = LocalContext.current;
    var isDropdownExpanded by remember {
        mutableStateOf(false)
    }

    if (!GlobalState.selectedTask?.dueDate.isNullOrEmpty()) {
        initialTaskDate =
            GlobalState.selectedTask!!.dueDate!!.slice(5..6) + "/" +
                    GlobalState.selectedTask!!.dueDate!!.slice(8..9) + "/" +
                    GlobalState.selectedTask!!.dueDate!!.slice(0..3)
    }


    var taskName by remember {
        mutableStateOf(initialTaskName)
    }
    var listId by remember {
        mutableStateOf(initialListId)
    }
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = getDateFromDateString(initialTaskDate)?.time)
    var isProcessing by remember {
        mutableStateOf(false)
    }
    var blockDismissModal by remember {
        mutableStateOf(true)
    }

    val tags = remember {
        mutableStateListOf<Int>()
    }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (GlobalState.selectedTask == null) {
            focusRequester.requestFocus()
        } else {
            tags.addAll(GlobalState.selectedTask!!.tagIds)
        }
    }

    val toastContext = LocalContext.current

    @SuppressLint("SimpleDateFormat")
    fun handleSubmitTask() {
        isProcessing = true

        var formattedDateTime: String? = null

        if (datePickerState.selectedDateMillis != null) {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            formattedDateTime = sdf.format(datePickerState.selectedDateMillis)
        }
        println("formattedDateTime: $formattedDateTime")

        val taskService = RetrofitBuilder.getRetrofit().create(TaskService::class.java);

        fun handleApiFailure() {
            isProcessing = false
            Toast.makeText(
                toastContext,
                "Unable to mark task as completed",
                Toast.LENGTH_SHORT
            )
                .show()
        }

        fun handleApiSuccess() {
            isProcessing = false
            RefreshCounter.refreshListCount++
            GlobalState.openModal = Modal.NONE
            GlobalState.selectedTask = null
        }


        if (GlobalState.selectedTask != null) {
            GlobalState.selectedTask!!.taskName = taskName
            GlobalState.selectedTask!!.dueDate = formattedDateTime
            GlobalState.selectedTask!!.tagIds = ArrayList(tags)
            taskService.updateTask(GlobalState.selectedTask!!.id, GlobalState.selectedTask!!)
                .enqueue(object : Callback<Unit> {
                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                        handleApiFailure()
                    }

                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                        handleApiSuccess()
                    }
                })
        } else {
            val taskRequestBody =
                CreateTaskRequestBody(taskName, formattedDateTime, listId, ArrayList(tags))
            taskService.createTask(taskRequestBody).enqueue(object : Callback<Unit> {
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    handleApiFailure()
                }

                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    handleApiSuccess()
                }
            })
        }
    }


    Dialog(
        onDismissRequest = {
            GlobalState.openModal = Modal.NONE
            GlobalState.selectedTask = null
        },
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = !blockDismissModal),
    ) {
        Surface(shape = RoundedCornerShape(16.dp)) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .width(LocalConfiguration.current.screenWidthDp.dp - 40.dp)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionHeader(text = if (GlobalState.selectedTask == null)  "Create Task" else "Edit Task")
                    CustomDropdown(
                        label = "List",
                        value = arrayListOf(listId),
                        dropdownOptions = ArrayList(GlobalState.lists.map { DropdownOption(it.id, it.listName) }),
                        onChange = { listId = it.id as Int },
                        onOpenStatusChange = {
                            val handler = Handler(Looper.getMainLooper())
                            handler.postDelayed({
                                blockDismissModal = it
                            }, 100)
                        }
                    )

                    OutlinedTextField(
                        value = taskName,
                        onValueChange = { taskName = it },
                        label = { Text("Task Name") },
                        keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences),
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .fillMaxWidth()
                    )

                    CustomDatePicker(datePickerState = datePickerState)

                    if (GlobalState.tags.size > 0) {
                        CustomDropdown(
                            label = "Tags",
                            value = tags,
                            dropdownOptions = ArrayList(GlobalState.tags.map { DropdownOption(it.id, it.tagName)}),
                            onChange = {
                                if (tags.contains(it.id)) {
                                    tags.remove(it.id)
                                } else {
                                    tags.add(it.id as Int)
                                }
                            },
                            closeOnClick = false,
                            onOpenStatusChange = {
                                val handler = Handler(Looper.getMainLooper())
                                handler.postDelayed({
                                    blockDismissModal = it
                                }, 100)
                            }
                        )
                    }


                    Button(
                        onClick = { handleSubmitTask() },
                        enabled = !isProcessing && listId != null,
                        modifier = Modifier.align(alignment = Alignment.End)
                    ) {
                        var buttonText =
                            if (GlobalState.selectedTask != null) "Update Task" else "Create Task"

                        if (isProcessing) {
                            buttonText = "Processing..."
                        }

                        Text(buttonText)
                    }
                }
            }
        }
    }
}