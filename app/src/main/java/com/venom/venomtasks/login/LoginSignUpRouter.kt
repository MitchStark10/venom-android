package com.venom.venomtasks.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginSignUpRouter(handleSuccessfulLogin: (String) -> Unit) {
    var showLoginForm by remember {
        mutableStateOf(true)
    }
    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }

    fun onUserEmailChange(newEmail: String) {
        userEmail = newEmail
    }

    fun onUserPasswordChange(newPassword: String) {
        userPassword = newPassword
    }

    if (showLoginForm) {
        LoginForm(
            handleSuccessfulLogin,
            userEmail,
            userPassword,
            ::onUserEmailChange,
            ::onUserPasswordChange,
            showSignUp = { showLoginForm = false }
        )
    } else {
        SignUpForm(
            handleSuccessfulLogin,
            userEmail,
            userPassword,
            ::onUserEmailChange,
            ::onUserPasswordChange,
            showLogin = { showLoginForm = true }
        )
    }
}