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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.venom.venomtasks.classes.AutoDeleteOptions
import com.venom.venomtasks.classes.EditSettingsBody
import com.venom.venomtasks.classes.GlobalState
import com.venom.venomtasks.classes.SettingsResponse
import com.venom.venomtasks.classes.Views
import com.venom.venomtasks.components.CustomDropdown
import com.venom.venomtasks.components.DropdownOption
import com.venom.venomtasks.services.ListService
import com.venom.venomtasks.services.RetrofitBuilder
import com.venom.venomtasks.services.SetLoginState
import com.venom.venomtasks.services.UserService
import com.venom.venomtasks.utils.getSharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SettingsView() {
    var email by remember {
        mutableStateOf("")
    }
    var autoDeleteTasksValue by remember {
        mutableStateOf("")
    }
    var standupListsValue = remember {
        mutableStateListOf<Int>()
    }
    var isProcessing by remember {
        mutableStateOf(true)
    }
    val currentContext = LocalContext.current
    val sharedPreferences = getSharedPreferences(currentContext)
    val setLoginState = SetLoginState.current

    LaunchedEffect(GlobalState.selectedView) {
        if (GlobalState.selectedView != Views.SETTINGS) {
            return@LaunchedEffect
        }

        val userService = RetrofitBuilder.getRetrofit().create(UserService::class.java)
        standupListsValue.clear()
        standupListsValue.addAll(GlobalState.lists.filter { it.isStandupList }.map { it.id })
        userService.getSettings().enqueue(object: Callback<SettingsResponse> {
            override fun onResponse(
                call: Call<SettingsResponse>,
                response: Response<SettingsResponse>
            ) {
                val settingsResponse = response.body()

                if (settingsResponse == null) {
                    Toast.makeText(
                        currentContext,
                        "Unable to fetch settings",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    isProcessing = false
                    return
                }
                autoDeleteTasksValue = settingsResponse.autoDeleteTasks
                email = settingsResponse.email
            }

            override fun onFailure(call: Call<SettingsResponse>, t: Throwable) {
                // Toast
                Toast.makeText(
                    currentContext,
                    "Unable to fetch settings",
                    Toast.LENGTH_SHORT
                )
                    .show()
                isProcessing = false
            }
        })
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Standup Settings")
        CustomDropdown(
            label = "Auto-Delete Tasks",
            value = arrayListOf(autoDeleteTasksValue),
            dropdownOptions = arrayListOf(
                DropdownOption(AutoDeleteOptions.NEVER.value, "Never"),
                DropdownOption(AutoDeleteOptions.ONE_WEEK.value, "1 Week After Task Completion"),
                DropdownOption(AutoDeleteOptions.TWO_WEEKS.value, "2 Weeks After Task Completion"),
                DropdownOption(AutoDeleteOptions.ONE_MONTH.value, "1 Month After Task Completion")
            ),
            onChange = {
                val previousValue = autoDeleteTasksValue
                autoDeleteTasksValue = it.id as String
                val userService = RetrofitBuilder.getRetrofit().create(UserService::class.java)
                userService.editSettings(EditSettingsBody(it.id)).enqueue(object: Callback<Unit> {
                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                        Toast.makeText(currentContext, "Failed to update auto-deletion cadence", Toast.LENGTH_SHORT).show()
                        autoDeleteTasksValue = previousValue
                    }

                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                        Toast.makeText(currentContext, "Successfully updated auto-deletion cadence", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        )
        CustomDropdown(
            label = "Standup Lists",
            value = standupListsValue,
            dropdownOptions = ArrayList(GlobalState.lists.map { DropdownOption(it.id, it.listName) }),
            onChange = {
                val listToUpdate = GlobalState.lists.find { list -> list.id == it.id }
                if (listToUpdate == null) {
                    Toast.makeText(currentContext, "An error occurred while updating the list setting.", Toast.LENGTH_SHORT).show()
                    return@CustomDropdown
                }

                if (standupListsValue.contains(it.id)) {
                    standupListsValue.remove(it.id)
                } else {
                    standupListsValue.add(it.id as Int)
                }

                val originalValue = listToUpdate.isStandupList
                listToUpdate.isStandupList = !listToUpdate.isStandupList

                val listService = RetrofitBuilder.getRetrofit().create(ListService::class.java)
                listService.updateList(listToUpdate.id, listToUpdate).enqueue(object: Callback<Unit> {
                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                        println("t")
                        println(t)
                        Toast.makeText(currentContext, "Unable to update standup lists.", Toast.LENGTH_SHORT).show()
                        listToUpdate.isStandupList = originalValue
                    }

                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                        print("resposne")
                        println(response)
                        Toast.makeText(currentContext, "Standup lists updated successfully.", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        )
        Divider()
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = {
                sharedPreferences.edit().remove("token").apply()
                setLoginState(false)
            }, modifier = Modifier.padding(end = 16.dp)) {
                Text("Logout")
            }
            Text("Currently signed-in as: $email")
        }
    }
}