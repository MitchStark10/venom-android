package com.example.venom.layout

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.venom.classes.SelectedView
import com.example.venom.classes.Views

@Composable
fun LayoutRouter() {
    when (SelectedView.selectedView) {
        Views.TODAY -> Text("Today")
        Views.UPCOMING -> Text("Upcoming")
        Views.COMPLETED -> Text("Completed")
        Views.LIST -> Text("List")
    }
}