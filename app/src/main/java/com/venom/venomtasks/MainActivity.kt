package com.venom.venomtasks

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.venom.venomtasks.classes.LogTag
import com.venom.venomtasks.layout.LayoutRouter
import com.venom.venomtasks.layout.NavigationDrawer
import com.venom.venomtasks.login.LoginSignUpRouter
import com.venom.venomtasks.services.RetrofitBuilder
import com.venom.venomtasks.services.SetLoginState
import com.venom.venomtasks.ui.theme.VenomTheme


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPrefs =
            com.venom.venomtasks.utils.getSharedPreferences(applicationContext)

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

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
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
        Log.i(LogTag.MAIN_ACTIVITY, "Creating retrofit with token")
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
        CompositionLocalProvider(SetLoginState provides { newState -> isLoggedIn = newState} ) {
            Column {
                NavigationDrawer {
                    LayoutRouter()
                }
            }
        }
    }
}