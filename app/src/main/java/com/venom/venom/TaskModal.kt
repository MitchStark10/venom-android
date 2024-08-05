package com.venom.venom

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.venom.venom.classes.CreateTaskRequestBody
import com.venom.venom.classes.Modal
import com.venom.venom.classes.RefreshCounter
import com.venom.venom.classes.SelectedView
import com.venom.venom.components.CustomDatePicker
import com.venom.venom.services.RetrofitBuilder
import com.venom.venom.services.TaskService
import com.venom.venom.utils.getDateFromDateString
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskModal() {
    val initialTaskName = SelectedView.selectedTask?.taskName ?: ""
    var initialTaskDate: String? = null

    if (!SelectedView.selectedTask?.dueDate.isNullOrEmpty()) {
        initialTaskDate =
            SelectedView.selectedTask!!.dueDate!!.slice(5..6) + "/" +
                    SelectedView.selectedTask!!.dueDate!!.slice(8..9) + "/" +
                    SelectedView.selectedTask!!.dueDate!!.slice(0..3)
    }


    var taskName by remember {
        mutableStateOf(initialTaskName)
    }
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = getDateFromDateString(initialTaskDate)?.time)
    var isProcessing by remember {
        mutableStateOf(false)
    }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (SelectedView.selectedTask == null) {
            focusRequester.requestFocus()
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
            SelectedView.openModal = Modal.NONE
            SelectedView.selectedTask = null
        }


        if (SelectedView.selectedTask != null) {
            SelectedView.selectedTask!!.taskName = taskName
            SelectedView.selectedTask!!.dueDate = formattedDateTime
            taskService.updateTask(SelectedView.selectedTask!!.id, SelectedView.selectedTask!!)
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
                CreateTaskRequestBody(taskName, formattedDateTime, SelectedView.selectedList!!.id)
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
            SelectedView.openModal = Modal.NONE; SelectedView.selectedTask = null
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .width(LocalConfiguration.current.screenWidthDp.dp - 40.dp)
                .padding(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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

                Button(
                    onClick = { handleSubmitTask() },
                    enabled = !isProcessing,
                    modifier = Modifier.align(alignment = Alignment.End)
                ) {
                    var buttonText =
                        if (SelectedView.selectedTask != null) "Update Task" else "Create Task"

                    if (isProcessing) {
                        buttonText = "Processing..."
                    }

                    Text(buttonText)
                }
            }
        }
    }
}