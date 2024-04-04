package com.example.venom

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
fun NewTaskModal() {

    var taskName by remember {
        mutableStateOf("")
    }

    var dateState by remember {
        mutableStateOf(SimpleDateFormat("MMddyyyy").format(Calendar.getInstance().time))
    }

    var isProcessing by remember {
        mutableStateOf(false)
    }

    val toastContext = LocalContext.current

    fun handleSubmitNewTask() {
        isProcessing = true
        val initialDateStr =
            dateState.slice(0..1) + "/" + dateState.slice(2..3) + "/" + dateState.slice(4..7) + " 12:00"
        val date: Date = SimpleDateFormat("MM/dd/yyyy").parse(initialDateStr)
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        sdf.timeZone = TimeZone.getTimeZone("CET")
        val formattedDateTime = sdf.format(date)
        val newTaskBody =
            CreateTaskRequestBody(taskName, formattedDateTime, SelectedView.selectedList!!.id)
        val taskService = RetrofitBuilder.getRetrofit().create(TaskService::class.java);

        taskService.createTask(newTaskBody).enqueue(object : Callback<Unit> {
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                isProcessing = false
                Toast.makeText(
                    toastContext,
                    "Unable to mark task as completed",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                isProcessing = false
                RefreshCounter.refreshListCount++
                SelectedView.openModal = Modal.NONE
            }
        })

    }


    Dialog(onDismissRequest = { SelectedView.openModal = Modal.NONE }) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("Task Name") })

                DateTextField(
                    dateState = dateState,
                    setDateState = { dateState = it },
                    label = "Due Date"
                )

                val hasValidDateString = dateState.length == 8
                Button(
                    onClick = { handleSubmitNewTask() },
                    enabled = !isProcessing && hasValidDateString
                ) {
                    var buttonText = "Create Task"

                    if (isProcessing) {
                        buttonText = "Processing..."
                    }
                    Text(buttonText)
                }
            }
        }

    }
}