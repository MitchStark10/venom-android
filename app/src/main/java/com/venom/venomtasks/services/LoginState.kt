package com.venom.venomtasks.services

import androidx.compose.runtime.compositionLocalOf

val SetLoginState = compositionLocalOf<(Boolean) -> Unit> { error("No global state provided") }