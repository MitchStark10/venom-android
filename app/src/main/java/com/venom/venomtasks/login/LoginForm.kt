package com.venom.venomtasks.login

import android.util.Log
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.venomtasks.classes.LogTag
import com.venom.venomtasks.classes.LoginResponse
import com.venom.venomtasks.classes.User
import com.venom.venomtasks.services.UserService
import com.venom.venomtasks.services.RetrofitBuilder
import com.venom.venomtasks.services.autofill
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginForm(
    handleSuccessfulLogin: (String) -> Unit,
    userEmail: String,
    userPassword: String,
    onUserEmailChange: (String) -> Unit,
    onUserPasswordChange: (String) -> Unit,
    showSignUp: () -> Unit
) {
    var isProcessingApiCall by remember { mutableStateOf(false) }
    var loginFailureMessage by remember { mutableStateOf("") }

    val autofill = LocalAutofill.current

    fun handleButtonClick() {
        val userService: UserService =
            RetrofitBuilder.getRetrofit().create(UserService::class.java)
        isProcessingApiCall = true
        val userToLoginWith = User(userEmail, userPassword)
        loginFailureMessage = ""
        userService.login(userToLoginWith).enqueue(object : Callback<LoginResponse> {
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
                    Log.e(LogTag.LOGIN_FORM, "Did not receive token: " + response.code())
                    Log.e(LogTag.LOGIN_FORM, "Further details: " + response.body())
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
        Text("Venom", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = userEmail,
            onValueChange = { onUserEmailChange(it) },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.autofill(listOf(AutofillType.EmailAddress), onFill = onUserEmailChange)
        )
        OutlinedTextField(
            value = userPassword,
            onValueChange = { onUserPasswordChange(it) },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.autofill(listOf(AutofillType.Password), onFill = onUserPasswordChange)
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
        Text("Or")
        OutlinedButton(onClick = { showSignUp() },) {
            Text("Sign Up")
        }
    }
}