package com.venom.venomtasks.layout

import android.os.Build
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.venom.venomtasks.classes.List
import com.venom.venomtasks.classes.Modal
import com.venom.venomtasks.classes.RefreshCounter
import com.venom.venomtasks.classes.ReorderListsBody
import com.venom.venomtasks.classes.SelectedView
import com.venom.venomtasks.classes.Views
import com.venom.venomtasks.components.CenteredLoader
import com.venom.venomtasks.services.ListService
import com.venom.venomtasks.services.RetrofitBuilder
import com.venom.venomtasks.utils.getTitleText
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NavigationDrawer(
    content: @Composable() () -> Unit
) {
    val toastContext = LocalContext.current
    val view = LocalView.current;
    val lists = remember {
        mutableStateListOf<List>()
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState =
        rememberReorderableLazyListState(
            lazyListState = lazyListState
        ) { from, to ->
            lists.apply {
                add(to.index, removeAt(from.index))
            }

            view.performHapticFeedback(HapticFeedbackConstants.SEGMENT_FREQUENT_TICK)
        }

    var isSettingsMenuExpanded by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }

    fun closeDrawer() {
        scope.launch() {
            drawerState.apply {
                if (isClosed) open() else close()
            }
        }
    }

    LaunchedEffect(RefreshCounter.refreshListCount) {
        println("Initializing list service")
        val listService: ListService =
            RetrofitBuilder.getRetrofit().create(ListService::class.java)
        listService.getLists().enqueue(object : Callback<ArrayList<List>> {
            override fun onFailure(call: Call<ArrayList<List>>, t: Throwable) {
                println("Received error when attempting to retrieve lists: $t")
                isLoading = false
            }

            override fun onResponse(
                call: Call<ArrayList<List>>,
                response: Response<ArrayList<List>>
            ) {
                println("Returned response: ${response.code()}")
                val listsResponseBody = response.body()
                if (response.isSuccessful && !listsResponseBody.isNullOrEmpty()) {
                    println("Successfully setting lists: " + listsResponseBody.size)
                    lists.clear()
                    lists.addAll(listsResponseBody)

                    if (SelectedView.selectedList != null) {

                        val updatedList =
                            listsResponseBody.find { it.id == SelectedView.selectedList!!.id }

                        SelectedView.selectedList = updatedList
                    }
                }
                isLoading = false
            }
        })
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    label = {
                        NavigationDrawerRow(text = "Today", icon = Icons.Filled.DateRange)
                    },
                    selected = SelectedView.selectedView == Views.TODAY,
                    onClick = {
                        SelectedView.selectedView = Views.TODAY
                        closeDrawer()
                    }
                )
                NavigationDrawerItem(
                    label = {
                        NavigationDrawerRow(text = "Upcoming", icon = Icons.Filled.ArrowForward)
                    },
                    selected = SelectedView.selectedView == Views.UPCOMING,
                    onClick = {
                        SelectedView.selectedView = Views.UPCOMING
                        closeDrawer()
                    }
                )
                NavigationDrawerItem(
                    label = {
                        NavigationDrawerRow(text = "Completed", icon = Icons.Filled.Done)
                    },
                    selected = SelectedView.selectedView == Views.COMPLETED,
                    onClick = {
                        SelectedView.selectedView = Views.COMPLETED
                        closeDrawer()
                    }
                )
                NavigationDrawerItem(label = {
                    NavigationDrawerRow(
                        text = "Add New List",
                        icon = Icons.Outlined.Add
                    )
                },
                    selected = false,
                    onClick = { SelectedView.openModal = Modal.LIST_MODAL }
                )
                Divider()

                LazyColumn(state = lazyListState, modifier = Modifier.fillMaxHeight()) {
                    items(lists, key = { it.id }) { list ->
                        ReorderableItem(
                            state = reorderableLazyListState,
                            key = list.id
                        ) {
                            NavigationDrawerItem(
                                label = {
                                    NavigationDrawerRow(
                                        text = list.listName,
                                        icon = Icons.Outlined.CheckCircle
                                    )
                                },
                                selected = SelectedView.selectedView == Views.LIST && SelectedView.selectedList == list,
                                onClick = {
                                    SelectedView.selectedView = Views.LIST
                                    SelectedView.selectedList = list
                                    closeDrawer()
                                },
                                modifier = Modifier.longPressDraggableHandle(
                                    onDragStarted = {
                                        view.performHapticFeedback(HapticFeedbackConstants.DRAG_START)
                                    },
                                    onDragStopped = {
                                        view.performHapticFeedback(HapticFeedbackConstants.GESTURE_END)

                                        val listService = RetrofitBuilder.getRetrofit()
                                            .create(ListService::class.java);

                                        listService.reorderLists(ReorderListsBody(ArrayList(lists)))
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
                        }

                    }
                }

                if (isLoading) {
                    CenteredLoader()
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            getTitleText(),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    navigationIcon = {
                        IconButton(onClick = {
                            closeDrawer()
                        }) {
                            Icon(
                                Icons.Filled.Menu,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    actions = {
                        if (SelectedView.selectedView == Views.LIST) {
                            IconButton(onClick = { isSettingsMenuExpanded = true }) {
                                Icon(
                                    Icons.Filled.Settings,
                                    contentDescription = "Settings",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }

                            DropdownMenu(
                                expanded = isSettingsMenuExpanded,
                                onDismissRequest = { isSettingsMenuExpanded = false }) {
                                DropdownMenuItem(text = {
                                    Text("Delete List")
                                }, onClick = {
                                    val listService = RetrofitBuilder.getRetrofit()
                                        .create(ListService::class.java);
                                    listService.deleteList(SelectedView.selectedList!!.id)
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
                                                isSettingsMenuExpanded = false;
                                                SelectedView.selectedView = Views.TODAY;
                                                SelectedView.selectedList = null;
                                                RefreshCounter.refreshListCount++;
                                            }
                                        })

                                })
                            }
                        }
                    }
                )
            }
        ) { contentPadding ->
            Column(Modifier.padding(top = contentPadding.calculateTopPadding())) {
                content()
            }
        }
    }
}