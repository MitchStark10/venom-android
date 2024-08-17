package com.venom.venomtasks

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.venom.venomtasks.classes.ListCreationRequestBody
import com.venom.venomtasks.classes.Modal
import com.venom.venomtasks.classes.RefreshCounter
import com.venom.venomtasks.classes.GlobalState
import com.venom.venomtasks.services.ListService
import com.venom.venomtasks.services.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ListModal() {
    val toastContext = LocalContext.current

    var listName by remember {
        mutableStateOf("")
    }
    var isProcessing by remember {
        mutableStateOf(false)
    }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    fun handleSubmitList() {
        isProcessing = true;
        val listService = RetrofitBuilder.getRetrofit().create(ListService::class.java);
        val requestBody = ListCreationRequestBody(listName)
        listService.createList(requestBody).enqueue(object : Callback<Unit> {
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                isProcessing = false;
                Toast.makeText(
                    toastContext,
                    "Unable to mark task as completed",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                RefreshCounter.refreshListCount++
                GlobalState.openModal = Modal.NONE
            }
        })
    }

    Dialog(
        onDismissRequest = {
            GlobalState.openModal = Modal.NONE; GlobalState.selectedTask = null
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
                    value = listName,
                    onValueChange = { listName = it },
                    label = { Text("List Name") },
                    keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences),
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth()
                )

                Button(
                    onClick = { handleSubmitList() },
                    enabled = !isProcessing && listName.isNotEmpty(),
                    modifier = Modifier.align(alignment = Alignment.End)
                ) {
                    var buttonText = "Create List"

                    if (isProcessing) {
                        buttonText = "Processing..."
                    }

                    Text(buttonText)
                }
            }
        }
    }
}