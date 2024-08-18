package com.venom.venomtasks.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.venom.venomtasks.classes.GlobalState

data class DropdownOption(val id: Any, val label: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdown(value: String, dropdownOptions: ArrayList<DropdownOption>, onChange: (newOption: DropdownOption) -> Unit) {
    var isDropdownExpanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(expanded = isDropdownExpanded, onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }) {
        TextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
        )

        ExposedDropdownMenu(expanded = isDropdownExpanded, onDismissRequest = { isDropdownExpanded = false }) {
            dropdownOptions.forEach { option ->
                DropdownMenuItem(text = { Text(option.label) }, onClick = {
                    onChange(option)
                    isDropdownExpanded = false
                })
            }
        }
    }
}