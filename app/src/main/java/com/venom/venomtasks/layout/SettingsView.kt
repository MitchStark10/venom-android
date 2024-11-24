package com.venom.venomtasks.layout

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.venom.venomtasks.classes.AutoDeleteOptions
import com.venom.venomtasks.classes.GlobalState
import com.venom.venomtasks.classes.SettingsResponse
import com.venom.venomtasks.classes.Views
import com.venom.venomtasks.components.CustomDropdown
import com.venom.venomtasks.components.DropdownOption
import com.venom.venomtasks.services.RetrofitBuilder
import com.venom.venomtasks.services.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SettingsView() {
    var email = ""
    val autoDeleteTasksValue = remember {
        mutableStateListOf<String>()
    }
    var isProcessing by remember {
        mutableStateOf(true)
    }
    val toastContext = LocalContext.current;

    LaunchedEffect(GlobalState.selectedView) {
        if (GlobalState.selectedView != Views.SETTINGS) {
            return@LaunchedEffect;
        }

        val userSerivce = RetrofitBuilder.getRetrofit().create(UserService::class.java)
        userSerivce.getSettings().enqueue(object: Callback<SettingsResponse> {
            override fun onResponse(
                call: Call<SettingsResponse>,
                response: Response<SettingsResponse>
            ) {
                val settingsResponse = response.body()

                if (settingsResponse == null) {
                    Toast.makeText(
                        toastContext,
                        "Unable to fetch settings",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    isProcessing = false
                    return;
                }
                email = settingsResponse.email
            }

            override fun onFailure(call: Call<SettingsResponse>, t: Throwable) {
                // Toast
                Toast.makeText(
                    toastContext,
                    "Unable to fetch settings",
                    Toast.LENGTH_SHORT
                )
                    .show()
                isProcessing = false;
            }
        })
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Standup Settings")
        CustomDropdown(
            label = "Auto-Delete Tasks",
            value = autoDeleteTasksValue,
            dropdownOptions = arrayListOf(
                DropdownOption(AutoDeleteOptions.NEVER.value, "Never")
            ),
            onChange = {
                if (autoDeleteTasksValue.contains(it.id
                )) {
                    autoDeleteTasksValue.remove(it.id);
                } else {
                    autoDeleteTasksValue.add(it.id as String)
                }
            }
        )
        CustomDropdown(
            label = "Standup Lists",
            value = GlobalState.lists.filter { it.isStandupList }.map { it.id },
            dropdownOptions = ArrayList(GlobalState.lists.map { DropdownOption(it.id, it.listName) }),
            onChange = { }
        )
        Divider()
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { }, modifier = Modifier.padding(end = 16.dp)) {
                Text("Logout")
            }
            Text("Currently signed-in as: $email")
        }
    }
}