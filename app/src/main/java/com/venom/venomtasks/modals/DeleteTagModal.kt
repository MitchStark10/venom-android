package com.venom.venomtasks.modals

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.venom.venomtasks.classes.GlobalState
import com.venom.venomtasks.classes.Modal
import com.venom.venomtasks.classes.RefreshCounter
import com.venom.venomtasks.classes.ReorderTagsBody
import com.venom.venomtasks.services.RetrofitBuilder
import com.venom.venomtasks.services.TagService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun DeleteTagModal() {

    val toastContext = LocalContext.current
    var isProcessing by remember {
        mutableStateOf(false)
    }

    fun closeModal() {
        GlobalState.openModal = Modal.NONE
        GlobalState.selectedTask = null
        GlobalState.selectedTag = null
    }

    fun handleDeleteTag() {
        isProcessing = true
         val tagService = RetrofitBuilder.getRetrofit()
                    .create(TagService::class.java);

        tagService.deleteTag(GlobalState.selectedTag!!.id)
            .enqueue(object : Callback<Unit> {
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Toast.makeText(
                        toastContext,
                        "Unable to delete tag",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    if (response.isSuccessful) {
                        RefreshCounter.refreshListCount++;
                    } else {
                        println("Response: $response")
                    }
                }
            })

        isProcessing = false
        closeModal()
    }

    Dialog(
        onDismissRequest = ::closeModal,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {

        Surface(shape = RoundedCornerShape(16.dp)) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .width(LocalConfiguration.current.screenWidthDp.dp - 40.dp)
                    .padding(16.dp)
            ) {
                Column {
                    Text("Are you sure you want to delete this tag?")
                    Button(
                        onClick = { handleDeleteTag() },
                        modifier = Modifier.align(alignment = Alignment.End)
                    ) {

                        var buttonText = "Delete Tag"

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