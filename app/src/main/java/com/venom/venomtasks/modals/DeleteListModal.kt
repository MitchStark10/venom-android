package com.venom.venomtasks.modals

import android.provider.Settings.Global
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.venom.venomtasks.classes.GlobalState
import com.venom.venomtasks.classes.Modal
import com.venom.venomtasks.classes.RefreshCounter
import com.venom.venomtasks.classes.Views
import com.venom.venomtasks.services.ListService
import com.venom.venomtasks.services.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun DeleteListModal() {
    val toastContext = LocalContext.current

    val onCancel = {
        GlobalState.openModal = Modal.NONE
    }

    val onConfirm = {
        val listService = RetrofitBuilder.getRetrofit()
            .create(ListService::class.java);
        listService.deleteList(GlobalState.selectedList!!.id)
            .enqueue(object : Callback<Unit> {
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Toast.makeText(
                        toastContext,
                        "Unable to delete list",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    GlobalState.openModal = Modal.NONE
                    GlobalState.selectedView = Views.TODAY;
                    GlobalState.selectedList = null;
                    RefreshCounter.refreshListCount++;
                }
            })
    }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Delete List") },
        text = { Text("Are you sure you want to delete this list?") },
        confirmButton = { Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(
            containerColor = Color.Red,
            contentColor = Color.White
        )) { Text("Yes, delete") } },
        dismissButton = { Button(onClick = onCancel) { Text("Cancel") } }
    )
}