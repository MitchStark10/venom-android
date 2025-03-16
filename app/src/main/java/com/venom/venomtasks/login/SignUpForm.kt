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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venom.venomtasks.classes.LogTag
import com.venom.venomtasks.classes.LoginResponse
import com.venom.venomtasks.classes.User
import com.venom.venomtasks.components.PasswordTextField
import com.venom.venomtasks.services.UserService
import com.venom.venomtasks.services.RetrofitBuilder
import com.venom.venomtasks.services.autofill
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignUpForm(
    handleSuccessfulLogin: (String) -> Unit,
    userEmail: String,
    userPassword: String,
    onUserEmailChange: (String) -> Unit,
    onUserPasswordChange: (String) -> Unit,
    showLogin: () -> Unit,
) {
    var confirmUserPassword by remember { mutableStateOf("") }
    var isProcessingApiCall by remember { mutableStateOf(false) }
    var signUpFailureMessage by remember { mutableStateOf("") }

    fun handleButtonClick() {
        if (userPassword != confirmUserPassword) {
            signUpFailureMessage = "Please make sure your passwords match"
            return
        }

        val userService: UserService =
            RetrofitBuilder.getRetrofit().create(UserService::class.java)
        isProcessingApiCall = true
        val newUser = User(userEmail, userPassword)
        signUpFailureMessage = ""
        userService.createUser(newUser).enqueue(object : Callback<LoginResponse> {
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
                    val errorBody = response.errorBody()
                    val errorConverter = RetrofitBuilder.getRetrofit().responseBodyConverter<LoginResponse>(
                        LoginResponse::class.java, arrayOfNulls(0)
                    )

                    val errorResponse = try {
                        errorConverter.convert(errorBody!!)
                    } catch (e: Exception) {
                        Log.e(LogTag.SIGNUP_FORM, "Error converting error response: $e")
                        null
                    }

                    Log.e(LogTag.SIGNUP_FORM, "Did not receive token: " + response.code())
                    Log.e(LogTag.SIGNUP_FORM, "Further details: $errorResponse")
                    signUpFailureMessage = if (!errorResponse?.error.isNullOrEmpty()) {
                        errorResponse!!.error!!
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
            onValueChange = { onUserEmailChange(it) },
            label = { Text("Email") },
            modifier = Modifier.autofill(listOf(AutofillType.EmailAddress), onFill = onUserEmailChange)
        )
        PasswordTextField(value = userPassword, onValueChange = onUserPasswordChange)
        PasswordTextField(value = confirmUserPassword, onValueChange = { confirmUserPassword = it }, label = "Confirm Password")
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