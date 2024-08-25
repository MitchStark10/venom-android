package com.venom.venomtasks.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun LoginSignUpRouter(handleSuccessfulLogin: (String) -> Unit) {
    var showLoginForm by remember {
        mutableStateOf(true)
    }

    if (showLoginForm) {
        LoginForm(handleSuccessfulLogin, showSignUp = { showLoginForm = false })
    } else {
        SignUpForm(handleSuccessfulLogin, showLogin = { showLoginForm = true })
    }
}