package com.venom.venomtasks.layout

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.venom.venomtasks.classes.GlobalState
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.venom.venomtasks.components.TagPill

@Composable
fun TagView() {
    LazyColumn(modifier = Modifier.padding(top = 16.dp, start = 8.dp)) {
        items(GlobalState.tags, key = { it.id }) { tag ->
            TagPill(tag = tag, onClick = { }, allowDeleteTag = true)
            Spacer(modifier = Modifier.size(16.dp))
        }
    }
}