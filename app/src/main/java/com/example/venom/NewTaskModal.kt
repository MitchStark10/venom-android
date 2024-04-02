package com.example.venom

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import com.example.venom.classes.Modal
import com.example.venom.classes.SelectedView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskModal() {
    var taskName by remember {
        mutableStateOf("")
    }

    val dateState = rememberDatePickerState()


    Dialog(onDismissRequest = { SelectedView.openModal = Modal.NONE }) {
        Column {
            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                label = { Text("Task Name") })
            
            DatePicker(state = dateState, title = { Text("Due Date") })
            Button(onClick = { /*TODO*/ }) {
                Text("Create Task")
            }
        }
    }
}