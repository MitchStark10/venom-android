package com.example.venom.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.venom.classes.List
import com.example.venom.classes.Modal
import com.example.venom.classes.RefreshCounter
import com.example.venom.classes.SelectedView
import com.example.venom.classes.Views
import com.example.venom.components.CenteredLoader
import com.example.venom.services.ListService
import com.example.venom.services.RetrofitBuilder
import com.example.venom.utils.getTitleText
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawer(
    content: @Composable() () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val lists = remember {
        mutableStateListOf<List>()
    }
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
                Divider()
                for (list in lists) {
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
                        })
                }

                if (isLoading) {
                    CenteredLoader()
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                    NavigationDrawerItem(label = {
                        NavigationDrawerRow(
                            text = "Add New List",
                            icon = Icons.Outlined.Add
                        )
                    },
                        selected = false,
                        onClick = { SelectedView.openModal = Modal.LIST_MODAL }
                    )
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
                )
            }
        ) { contentPadding ->
            Column(Modifier.padding(top = contentPadding.calculateTopPadding())) {
                content()
            }
        }
    }
}