package com.example.venom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.venom.login.LoginForm
import com.example.venom.ui.theme.VenomTheme

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VenomTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContainer()
                }
            }
        }
    }
}

@Composable
fun AppContainer() {
    var isLoggedIn by remember {
        mutableStateOf(false)
    }

    var token by remember {
        mutableStateOf("")
    }

    fun handleSuccessfulLogin(newToken: String) {
        isLoggedIn = true
        token = newToken
    }

    if (!isLoggedIn) {
        LoginForm(::handleSuccessfulLogin)
    } else {
        Text("You've made it past the login screen!")
    }
}