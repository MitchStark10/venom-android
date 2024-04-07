package com.example.venom.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.venom.classes.DateTransformation


const val maxChars = 8

@Composable
fun DateTextField(dateState: String, setDateState: (newVal: String) -> Unit, label: String) {
    OutlinedTextField(
        value = dateState,
        onValueChange = {
            if (it.length <= maxChars) {
                setDateState(it)
            }
        },
        visualTransformation = DateTransformation(),
        singleLine = true,
        label = { Text(label) },
        placeholder = { Text(text = "MM/DD/YYYY") }
    )
}