package com.example.venom.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.venom.login.ui.theme.VenomTheme

@Composable
fun LoginForm() {
    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var isProcessingApiCall by remember { mutableStateOf(false) }

    fun handleButtonClick() {
        isProcessingApiCall = true

        // TODO: Make the login api call

    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Venom")
        TextField(value = userEmail, onValueChange = { userEmail = it }, label = { Text("Email") })
        TextField(
            value = userPassword,
            onValueChange = { userPassword = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )
        Button(onClick = { handleButtonClick() }, enabled = !isProcessingApiCall) {
            var textForButton = "Log In"
            if (isProcessingApiCall) {
                textForButton = "Processing..."
            }
            Text(textForButton)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginFormPreview() {
    VenomTheme {
        LoginForm()
    }
}