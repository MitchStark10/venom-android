package com.venom.venomtasks.layout

import android.os.Build
import android.util.Log
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.venom.venomtasks.classes.GlobalState
import com.venom.venomtasks.classes.List
import com.venom.venomtasks.classes.LogTag
import com.venom.venomtasks.classes.Modal
import com.venom.venomtasks.classes.RefreshCounter
import com.venom.venomtasks.classes.ReorderListsBody
import com.venom.venomtasks.classes.SettingsResponse
import com.venom.venomtasks.classes.Tag
import com.venom.venomtasks.classes.Views
import com.venom.venomtasks.components.CenteredLoader
import com.venom.venomtasks.services.ListService
import com.venom.venomtasks.services.RetrofitBuilder
import com.venom.venomtasks.services.TagService
import com.venom.venomtasks.services.UserService
import com.venom.venomtasks.utils.getDateStringFromMillis
import com.venom.venomtasks.utils.getTitleText
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.util.Date
import java.util.TimeZone

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NavigationDrawer(
    content: @Composable() () -> Unit
) {
    val toastContext = LocalContext.current
    val view = LocalView.current;
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val lazyListState = rememberLazyListState()
    var titleText by remember {
        mutableStateOf(getTitleText())
    }
    val reorderableLazyListState =
        rememberReorderableLazyListState(
            lazyListState = lazyListState
        ) { from, to ->
            GlobalState.lists.apply {
                add(to.index, removeAt(from.index))
            }

            view.performHapticFeedback(HapticFeedbackConstants.SEGMENT_FREQUENT_TICK)
        }

    var isSettingsMenuExpanded by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val currentContext = LocalContext.current

    var isLoading by remember { mutableStateOf(true) }

    fun closeDrawer() {
        scope.launch() {
            drawerState.apply {
                if (isClosed) open() else close()
            }
        }
    }

    LaunchedEffect(RefreshCounter.refreshListCount) {
        val listService: ListService =
            RetrofitBuilder.getRetrofit().create(ListService::class.java)
        listService.getLists(
            getDateStringFromMillis(
                Date().time,
                "yyyy-MM-dd",
                TimeZone.getDefault().id
            )
        ).enqueue(object : Callback<ArrayList<List>> {
            override fun onFailure(call: Call<ArrayList<List>>, t: Throwable) {
                Log.e(LogTag.NAVIGATION_DRAWER, "Received error when attempting to retrieve lists: $t")
                isLoading = false
            }

            override fun onResponse(
                call: Call<ArrayList<List>>,
                response: Response<ArrayList<List>>
            ) {
                val listsResponseBody = response.body()
                if (response.isSuccessful && !listsResponseBody.isNullOrEmpty()) {
                    Log.i(LogTag.NAVIGATION_DRAWER, "Successfully setting lists: " + listsResponseBody.size)
                    GlobalState.lists.clear()
                    GlobalState.lists.addAll(listsResponseBody)

                    if (GlobalState.selectedList != null) {
                        val updatedList =
                            listsResponseBody.find { it.id == GlobalState.selectedList!!.id }

                        GlobalState.selectedList = updatedList
                    }
                }

                isLoading = false
            }
        })

        val tagService: TagService = RetrofitBuilder.getRetrofit().create(TagService::class.java)
        tagService.getTags().enqueue(object: Callback<ArrayList<Tag>> {
            override fun onFailure(call: Call<ArrayList<Tag>>, t: Throwable) {
                Log.e(LogTag.NAVIGATION_DRAWER, "Received error when attempting to retrieve tags: $t")
            }

            override fun onResponse(
                call: Call<ArrayList<Tag>>,
                response: Response<ArrayList<Tag>>
            ) {
                val tags = response.body()
                if (response.isSuccessful && !tags.isNullOrEmpty()) {
                    GlobalState.tags.clear();
                    GlobalState.tags.addAll(tags)
                }
            }
        })
    }

    LaunchedEffect(GlobalState.selectedList?.listName, GlobalState.selectedView) {
        titleText = getTitleText()
    }

    LaunchedEffect(Unit) {
        val userService = RetrofitBuilder.getRetrofit().create(UserService::class.java)
        userService.getSettings().enqueue(object: Callback<SettingsResponse> {
            override fun onFailure(call: Call<SettingsResponse>, t: Throwable) {
                Toast.makeText(
                    currentContext,
                    "Unable to fetch settings",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onResponse(
                call: Call<SettingsResponse>,
                response: Response<SettingsResponse>
               ) {
                val settingsResponse = response.body();
                GlobalState.settingsResponse = settingsResponse
            }
        })
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.offset(x = (-5).dp)) {
                NavigationDrawerItem(
                    label = {
                        NavigationDrawerRow(text = "Today", icon = Icons.Filled.DateRange)
                    },
                    selected = GlobalState.selectedView == Views.TODAY,
                    onClick = {
                        GlobalState.selectedView = Views.TODAY
                        GlobalState.selectedList = null;
                        closeDrawer()
                    }
                )
                NavigationDrawerItem(
                    label = {
                        NavigationDrawerRow(text = "Upcoming", icon = Icons.Filled.ArrowForward)
                    },
                    selected = GlobalState.selectedView == Views.UPCOMING,
                    onClick = {
                        GlobalState.selectedView = Views.UPCOMING
                        GlobalState.selectedList = null;
                        closeDrawer()
                    }
                )
                NavigationDrawerItem(
                    label = {
                        NavigationDrawerRow(text = "Daily Report", icon = Icons.Filled.Star)
                    },
                    selected = GlobalState.selectedView == Views.STANDUP,
                    onClick = {
                        GlobalState.selectedView = Views.STANDUP
                        GlobalState.selectedList = null;
                        closeDrawer()
                    }

                )
                NavigationDrawerItem(
                    label = {
                        NavigationDrawerRow(text = "Completed", icon = Icons.Filled.Done)
                    },
                    selected = GlobalState.selectedView == Views.COMPLETED,
                    onClick = {
                        GlobalState.selectedView = Views.COMPLETED
                        GlobalState.selectedList = null;
                        closeDrawer()
                    }
                )

                NavigationDrawerItem(label = {
                    NavigationDrawerRow(text = "Tags", icon = Icons.Filled.Build)
                }, selected = GlobalState.selectedView == Views.TAGS, onClick = {
                    GlobalState.selectedView = Views.TAGS;
                    GlobalState.selectedList = null;
                    closeDrawer()
                })

                Divider()

                LazyColumn(state = lazyListState, modifier = Modifier.weight(1F)) {
                    items(GlobalState.lists, key = { it.id }){ list ->
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
                                selected = GlobalState.selectedView == Views.LIST && GlobalState.selectedList == list,
                                onClick = {
                                    GlobalState.selectedView = Views.LIST
                                    GlobalState.selectedList = list
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

                                        listService.reorderLists(ReorderListsBody(ArrayList(GlobalState.lists)))
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

                Divider()

                NavigationDrawerItem(label = {
                    NavigationDrawerRow(
                        text = "Add New List",
                        icon = Icons.Outlined.Add
                    )
                },
                    selected = false,
                    onClick = { GlobalState.openModal = Modal.CREATE_LIST_MODAL }
                )
                Divider()

                NavigationDrawerItem(
                    label = {
                        NavigationDrawerRow(text = "Settings", icon = Icons.Filled.Settings)
                    },
                    selected = GlobalState.selectedView == Views.SETTINGS,
                    onClick = {
                        GlobalState.selectedView = Views.SETTINGS
                        GlobalState.selectedList = null;
                        closeDrawer()
                    },
                    modifier = Modifier.zIndex(3F)
                )

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
                            titleText,
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
                        if (GlobalState.selectedView == Views.LIST) {
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
                                DropdownMenuItem(
                                    text = {
                                        Text("Rename List")
                                    }, onClick = {
                                        GlobalState.openModal = Modal.UPDATE_LIST_MODAL
                                        isSettingsMenuExpanded = false
                                    }
                                )
                                DropdownMenuItem(text = {
                                    Text("Delete List")
                                }, onClick = {
                                    GlobalState.openModal = Modal.DELETE_LIST_MODAL
                                    isSettingsMenuExpanded = false
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