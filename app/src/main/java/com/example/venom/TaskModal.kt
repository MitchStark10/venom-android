package com.example.venom

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.venom.classes.CreateTaskRequestBody
import com.example.venom.classes.Modal
import com.example.venom.classes.RefreshCounter
import com.example.venom.classes.SelectedView
import com.example.venom.components.DateTextField
import com.example.venom.services.RetrofitBuilder
import com.example.venom.services.TaskService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

@Composable
fun TaskModal() {
    val initialTaskName = SelectedView.selectedTask?.taskName ?: ""
    var initialTaskDate =
        if (SelectedView.selectedTask != null) "" else SimpleDateFormat("MMddyyyy").format(Calendar.getInstance().time)

    if (!SelectedView.selectedTask?.dueDate.isNullOrEmpty()) {
        initialTaskDate =
            SelectedView.selectedTask!!.dueDate!!.slice(5..6) +
                    SelectedView.selectedTask!!.dueDate!!.slice(8..9) +
                    SelectedView.selectedTask!!.dueDate!!.slice(0..3)
    }

    println("initial task date: " + SelectedView.selectedTask?.dueDate)

    var taskName by remember {
        mutableStateOf(initialTaskName)
    }
    var dateState by remember {
        mutableStateOf(initialTaskDate)
    }
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

    fun handleSubmitTask() {
        isProcessing = true

        var formattedDateTime: String? = null

        if (dateState.length == 8) {
            val initialDateStr =
                dateState.slice(0..1) + "/" + dateState.slice(2..3) + "/" + dateState.slice(4..7) + " 12:00"
            val date: Date = SimpleDateFormat("MM/dd/yyyy").parse(initialDateStr)
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            sdf.timeZone = TimeZone.getTimeZone("CET")
            formattedDateTime = sdf.format(date)
        }

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


    Dialog(onDismissRequest = {
        SelectedView.openModal = Modal.NONE; SelectedView.selectedTask = null
    }) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("Task Name") },
                    keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences),
                    modifier = Modifier.focusRequester(focusRequester)
                )

                DateTextField(
                    dateState = dateState,
                    setDateState = { dateState = it },
                    label = "Due Date",

                    )

                val hasValidDateString = dateState.isEmpty() || dateState.length == 8
                Button(
                    onClick = { handleSubmitTask() },
                    enabled = !isProcessing && hasValidDateString
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