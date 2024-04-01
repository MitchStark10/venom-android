package com.example.venom.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.venom.dataClasses.List
import com.example.venom.services.ListService
import com.example.venom.services.RetrofitBuilder
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
    var lists = remember {
        mutableStateListOf<List>()
    }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
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
                    println("Successfully setting lists")
                    lists = listsResponseBody.toMutableStateList()
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
                        Row {
                            Icon(Icons.Filled.DateRange, "Today")
                            Text(text = "Today")
                        }
                    },
                    selected = false,
                    onClick = { /*TODO*/ }
                )
                NavigationDrawerItem(
                    label = {
                        Row {
                            Icon(Icons.Filled.ArrowForward, "Upcoming")
                            Text(text = "Upcoming")
                        }
                    },
                    selected = false,
                    onClick = { /*TODO*/ }
                )
                NavigationDrawerItem(
                    label = {
                        Row {
                            Icon(Icons.Filled.Done, "Completed")
                            Text(text = "Completed")
                        }
                    },
                    selected = false,
                    onClick = { /*TODO*/ }
                )
                Divider()
                // TODO: Dynamic list retrieval
                NavigationDrawerItem(
                    label = { Text(text = "Lists coming soon: ${lists.size}") },
                    selected = false,
                    onClick = { /*TODO*/ })

                if (isLoading) {
                    CircularProgressIndicator()
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Venom", fontSize = 30.sp, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch() {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "")
                        }
                    },

                    )
            }
        ) { contentPadding ->
            Column(Modifier.padding(contentPadding)) {
                content()
            }
        }
    }
}