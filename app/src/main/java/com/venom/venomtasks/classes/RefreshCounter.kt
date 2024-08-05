package com.venom.venomtasks.classes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

class RefreshCounter {
    companion object {
        var refreshListCount: Int by mutableIntStateOf(0)
    }
}