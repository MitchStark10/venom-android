package com.venom.venomtasks.layout

import android.os.Build
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.venom.venomtasks.classes.GlobalState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.venom.venomtasks.classes.Modal
import com.venom.venomtasks.classes.RefreshCounter
import com.venom.venomtasks.classes.ReorderTagsBody
import com.venom.venomtasks.components.TagPill
import com.venom.venomtasks.services.RetrofitBuilder
import com.venom.venomtasks.services.TagService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun TagView() {

    val view = LocalView.current
    val toastContext = LocalContext.current
    val lazyListState = rememberLazyListState()

    val reorderableLazyListState =
        rememberReorderableLazyListState(
            lazyListState = lazyListState
        ) { from, to ->

            GlobalState.tags.apply {
                add(to.index, removeAt(from.index))
                view.performHapticFeedback(HapticFeedbackConstants.SEGMENT_FREQUENT_TICK)
            }
        }

    LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize()) {
        items(GlobalState.tags, key = { it.id }) { tag ->
            ReorderableItem(
                state = reorderableLazyListState,
                key = tag.id,
                enabled = true
            ) {
                TagPill(
                    tag = tag,
                    onClick = {
                        GlobalState.selectedTag = tag
                        GlobalState.openModal = Modal.TAG_MODAL
                    },
                    modifier =
                    Modifier.longPressDraggableHandle(
                        onDragStarted = {
                            view.performHapticFeedback(HapticFeedbackConstants.DRAG_START)
                        },
                        onDragStopped = {
                            view.performHapticFeedback(HapticFeedbackConstants.GESTURE_END)
                            val tagService = RetrofitBuilder.getRetrofit()
                                .create(TagService::class.java);

                            tagService.reorderTags(ReorderTagsBody(ArrayList(GlobalState.tags)))
                                .enqueue(object : Callback<Unit> {
                                    override fun onFailure(
                                        call: Call<Unit>,
                                        t: Throwable
                                    ) {
                                        Toast.makeText(
                                            toastContext,
                                            "Unable to save reordered lists",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    override fun onResponse(
                                        call: Call<Unit>,
                                        response: Response<Unit>
                                    ) {
                                        RefreshCounter.refreshListCount++
                                    }
                                })
                        },
                    )
                )
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
    }
}