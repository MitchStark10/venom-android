package com.venom.venomtasks.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.activity.ComponentActivity.MODE_PRIVATE

fun getSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("VENOM_SHARED_PREFS", MODE_PRIVATE)
}