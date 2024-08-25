package com.venom.venomtasks.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.venomtasks.classes.LoginResponse
import com.venom.venomtasks.classes.User
import com.venom.venomtasks.services.LoginService
import com.venom.venomtasks.services.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SignUpForm(handleSuccessfulLogin: (String) -> Unit, showLogin: () -> Unit) {
    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var confirmUserPassword by remember { mutableStateOf("") }
    var isProcessingApiCall by remember { mutableStateOf(false) }
    var signUpFailureMessage by remember { mutableStateOf("") }

    fun handleButtonClick() {
        if (userPassword != confirmUserPassword) {
            signUpFailureMessage = "Please make sure your passwords match"
            return
        }

        val loginService: LoginService =
            RetrofitBuilder.getRetrofit().create(LoginService::class.java)
        isProcessingApiCall = true
        val newUser = User(userEmail, userPassword)
        signUpFailureMessage = ""
        loginService.createUser(newUser).enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                isProcessingApiCall = false
                signUpFailureMessage = "Unexpected error occurred while logging in."
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
                    println("Further details: $responseBody")
                    signUpFailureMessage = if (!responseBody?.error.isNullOrEmpty()) {
                        responseBody!!.error!!
                    } else {
                        "An unknown error occurred"
                    }
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
        Text("Venom", fontSize = 30.sp, fontWeight = FontWeight.Bold)
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
        OutlinedTextField(
            value = confirmUserPassword,
            onValueChange = { confirmUserPassword = it },
            label = { Text("Confirm Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )
        Button(
            onClick = { handleButtonClick() },
            enabled = !isProcessingApiCall && userEmail.isNotEmpty() && userPassword.isNotEmpty()
        ) {
            var textForButton = "Sign Up"
            if (isProcessingApiCall) {
                textForButton = "Processing..."
            }
            Text(textForButton)
        }

        if (signUpFailureMessage.isNotEmpty()) {
            Text(signUpFailureMessage, color = Color.Red)
        }
        Text("Or")
        OutlinedButton(onClick = { showLogin() },) {
            Text("Log In")
        }
    }
}