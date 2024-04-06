package com.example.venom.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.venom.classes.Modal
import com.example.venom.classes.SelectedView
import com.example.venom.classes.Task

@Composable
fun TaskCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit),
    label: String,
    modifier: Modifier = Modifier,
    task: Task
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .padding(4.dp)
            .wrapContentWidth()
            .wrapContentSize(),
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onCheckedChange(it) }
        )

        Spacer(Modifier.size(6.dp))

        Text(
            text = label,
            overflow = TextOverflow.Visible,
            modifier = Modifier.clickable {
                SelectedView.selectedTask = task;
                SelectedView.openModal = Modal.TASK_MODAL
            }
        )
    }
}