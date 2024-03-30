package com.example.venom.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.venom.dataClasses.LoginResponse
import com.example.venom.dataClasses.User
import com.example.venom.login.ui.theme.VenomTheme
import com.example.venom.services.LoginService
import com.example.venom.services.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginForm(handleSuccessfulLogin: (String) -> Unit) {
    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var isProcessingApiCall by remember { mutableStateOf(false) }
    var loginFailureMessage by remember { mutableStateOf("") }

    fun handleButtonClick() {
        val loginService: LoginService = Retrofit.retrofit.create(LoginService::class.java)
        isProcessingApiCall = true
        val userToLoginWith = User(userEmail, userPassword)
        loginFailureMessage = ""
        loginService.login(userToLoginWith).enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                isProcessingApiCall = false
                loginFailureMessage = "Unexpected error occurred while logging in."
            }

            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody?.token != null && responseBody.token.isNotEmpty()) {
                    handleSuccessfulLogin(responseBody.token)
                } else {
                    println("Did not receive token: " + response.code())
                    println("Further details: " + response.body())
                    loginFailureMessage = "Email or password is incorrect"
                }

                isProcessingApiCall = false
            }
        })

    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Venom", fontSize = 30.sp)
        OutlinedTextField(
            value = userEmail,
            onValueChange = { userEmail = it },
            label = { Text("Email") })
        OutlinedTextField(
            value = userPassword,
            onValueChange = { userPassword = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = { handleButtonClick() },
            enabled = !isProcessingApiCall && userEmail.isNotEmpty() && userPassword.isNotEmpty()
        ) {
            var textForButton = "Log In"
            if (isProcessingApiCall) {
                textForButton = "Processing..."
            }
            Text(textForButton)
        }

        if (loginFailureMessage.isNotEmpty()) {
            Text(loginFailureMessage, color = Color.Red)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginFormPreview() {
    VenomTheme {
        LoginForm { }
    }
}