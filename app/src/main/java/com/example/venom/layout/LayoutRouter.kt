package com.example.venom.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.venom.classes.SelectedView
import com.example.venom.classes.Views
import com.example.venom.layout.listactivity.ListActivity

@Composable
fun LayoutRouter() {
    Box(modifier = Modifier.padding(10.dp)) {
        when (SelectedView.selectedView) {
            Views.TODAY -> Text("Today")
            Views.UPCOMING -> Text("Upcoming")
            Views.COMPLETED -> Text("Completed")
            Views.LIST -> ListActivity(list = SelectedView.selectedList!!)
        }
    }
}