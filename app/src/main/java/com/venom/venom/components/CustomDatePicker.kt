package com.venom.venom.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.venom.venom.utils.getDateStringFromMillis

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(datePickerState: DatePickerState) {
    var showDateCalendar by remember { mutableStateOf(false) }
    val dateText = if (datePickerState.selectedDateMillis != null) getDateStringFromMillis(
        datePickerState.selectedDateMillis!!
    ) else "No Date"
    val focusManager = LocalFocusManager.current

    Column {
        OutlinedTextField(
            dateText,
            onValueChange = { showDateCalendar = true },
            label = { Text(text = "Due Date") },
            trailingIcon = {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "Edit Date",
                    modifier = Modifier.clickable { datePickerState.setSelection(null) })
            },
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showDateCalendar = !showDateCalendar;
                    focusManager.clearFocus()
                },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                //For Icons
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        )

        if (showDateCalendar) {
            DatePicker(
                state = datePickerState,
                title = null,
                headline = null,
                showModeToggle = false,
            )
        }
    }
}