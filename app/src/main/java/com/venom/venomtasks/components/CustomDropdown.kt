package com.venom.venomtasks.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class DropdownOption(val id: Any, val label: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdown(label: String, value: List<Any>, dropdownOptions: ArrayList<DropdownOption>, onChange: (newOption: DropdownOption) -> Unit, closeOnClick: Boolean = true, onOpenStatusChange: (isOpen: Boolean) -> Unit = { }) {
    var isDropdownExpanded by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = isDropdownExpanded) {
        onOpenStatusChange(isDropdownExpanded)
    }

    val textValue = dropdownOptions.filter { value.contains(it.id) }.joinToString { it.label }

    ExposedDropdownMenuBox(expanded = isDropdownExpanded, onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }) {
        OutlinedTextField(
            label = { Text(label) },
            value = textValue,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
        )

        ExposedDropdownMenu(expanded = isDropdownExpanded, onDismissRequest = { isDropdownExpanded = false }) {
            dropdownOptions.forEach { option ->
                DropdownMenuItem(text = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(option.label)
                        if (value.contains(option.id)) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }, onClick = {
                    onChange(option)
                    if (closeOnClick) {
                        isDropdownExpanded = false
                    }
                })
            }
        }
    }
}