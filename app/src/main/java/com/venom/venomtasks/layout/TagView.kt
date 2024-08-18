package com.venom.venomtasks.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.venom.venomtasks.classes.GlobalState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.ui.graphics.Color
import com.venom.venomtasks.components.TagPill

@Composable
fun TagView() {
    LazyColumn {
        items(GlobalState.tags, key = { it.id }) { tag ->
            TagPill(tag = tag, onClick = { })
        }
    }
}