package com.example.venom.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun NavigationDrawerRow(text: String, icon: ImageVector) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(icon, text)
        Text(text)
    }
}