package com.venom.venomtasks.modals

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
import com.venom.venomtasks.classes.GlobalState
import com.venom.venomtasks.classes.Modal
import com.venom.venomtasks.classes.RefreshCounter
import com.venom.venomtasks.classes.TagCreationRequestBody
import com.venom.venomtasks.components.CustomDropdown
import com.venom.venomtasks.components.DropdownOption
import com.venom.venomtasks.services.RetrofitBuilder
import com.venom.venomtasks.services.TagService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val tagColorOptions = arrayListOf(
    DropdownOption("blue", "Blue"),
    DropdownOption("orange", "Orange"),
    DropdownOption("green", "Green"),
    DropdownOption("red", "Red")
)

@Composable
fun TagModal() {
    var tagName by remember {
        mutableStateOf("")
    }
    var tagColor by remember {
        mutableStateOf(tagColorOptions.first().id as String)
    }
    var isProcessing by remember {
        mutableStateOf(false)
    }
    val toastContext = LocalContext.current

    val focusRequester = remember { FocusRequester() }

    fun handleCreateTag() {
        val tagService = RetrofitBuilder.getRetrofit().create(TagService::class.java);
        val requestBody = TagCreationRequestBody(tagName, tagColor)
        tagService.createTags(requestBody).enqueue(object: Callback<Unit> {
            override fun onFailure(call: Call<Unit>, t: Throwable) {
                isProcessing = false;
                Toast.makeText(
                    toastContext,
                    "Unable to create tag",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    RefreshCounter.refreshListCount++
                    GlobalState.openModal = Modal.NONE
                }
            }
        })
    }

    Dialog(
        onDismissRequest = {
            GlobalState.openModal = Modal.NONE
            GlobalState.selectedTask = null
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
                    value = tagName,
                    onValueChange = { tagName = it },
                    label = { Text("Tag Name") },
                    keyboardOptions = KeyboardOptions(KeyboardCapitalization.Sentences),
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth()
                )

                CustomDropdown(value = tagColorOptions.find { it.id == tagColor}?.label ?: "", dropdownOptions = tagColorOptions, onChange = { tagColor = it.id as String })

                Button(
                    onClick = { handleCreateTag() },
                    enabled = !isProcessing && tagName.isNotEmpty(),
                    modifier = Modifier.align(alignment = Alignment.End)
                ) {
                    var buttonText = "Create Tag"

                    if (isProcessing) {
                        buttonText = "Processing..."
                    }

                    Text(buttonText)
                }
            }
        }
    }
}