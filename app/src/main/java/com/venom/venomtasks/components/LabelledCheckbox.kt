package com.venom.venomtasks.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.venomtasks.classes.Modal
import com.venom.venomtasks.classes.GlobalState
import com.venom.venomtasks.classes.Task

@Composable
fun TaskCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit),
    label: String,
    modifier: Modifier = Modifier,
    task: Task,
    showListName: Boolean = false
) {

    fun editTask() {
        GlobalState.selectedTask = task;
        GlobalState.openModal = Modal.TASK_MODAL
    }

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

        Column {
            Text(
                text = label,
                overflow = TextOverflow.Visible,
                modifier = Modifier.clickable {
                    editTask()
                }
            )

            if (showListName) {
                Text(
                    text = task.list!!.listName,
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                task.taskTag.forEach { taskTag ->
                    TagPill(tag = taskTag.tag, onClick = { editTask() })
                }
            }
        }

    }
}