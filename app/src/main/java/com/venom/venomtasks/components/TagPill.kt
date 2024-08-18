package com.venom.venomtasks.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.venom.venomtasks.classes.Tag
import com.venom.venomtasks.login.ui.theme.DarkBlue
import com.venom.venomtasks.login.ui.theme.DarkGreen
import com.venom.venomtasks.login.ui.theme.DarkOrange
import com.venom.venomtasks.login.ui.theme.DarkRed

val tagColorMap = mapOf(
    "green" to DarkGreen,
    "orange" to DarkOrange,
    "blue" to DarkBlue,
    "red" to DarkRed
)

@Composable
fun TagPill(tag: Tag, onClick: () -> Unit = { /* noop */  }) {
    val containerColor = tagColorMap[tag.tagColor] ?: Color.Blue

    SuggestionChip(onClick = onClick,
        label = { Text(tag.tagName) },
        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = containerColor, labelColor = Color.White ))
}