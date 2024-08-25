package com.venom.venomtasks

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.venom.venomtasks.layout.LayoutRouter
import com.venom.venomtasks.layout.NavigationDrawer
import com.venom.venomtasks.login.LoginForm
import com.venom.venomtasks.login.LoginSignUpRouter
import com.venom.venomtasks.services.RetrofitBuilder
import com.venom.venomtasks.ui.theme.VenomTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPrefs =
            applicationContext.getSharedPreferences("VENOM_SHARED_PREFS", MODE_PRIVATE)

        enableEdgeToEdge()

        setContent {
            VenomTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContainer(sharedPrefs)
                }
            }
        }
    }
}

@Composable
fun AppContainer(sharedPreferences: SharedPreferences) {
    val initialToken = sharedPreferences.getString("token", "")
    var isLoggedIn by remember {
        mutableStateOf(!initialToken.isNullOrEmpty())
    }
    var token by remember {
        mutableStateOf(initialToken)
    }

    LaunchedEffect(token) {
        println("Creating retrofit with token")
        RetrofitBuilder.createRetrofit(token)
    }

    fun handleSuccessfulLogin(newToken: String) {
        isLoggedIn = true
        token = newToken
        sharedPreferences.edit().putString("token", newToken).apply()
    }

    if (!isLoggedIn) {
        LoginSignUpRouter(::handleSuccessfulLogin)
    } else {
        Column {
            NavigationDrawer {
                LayoutRouter()
            }
        }
    }
}