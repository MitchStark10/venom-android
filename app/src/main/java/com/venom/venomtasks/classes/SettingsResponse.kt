package com.venom.venomtasks.classes

enum class AutoDeleteOptions(val value: String) {
    NEVER("-1"),
    ONE_WEEK("7"),
    TWO_WEEKS("14"),
    ONE_MONTH("30")
}

data class SettingsResponse(
    val email: String,
    val autoDeleteTasks: String
)

data class EditSettingsBody(
    val autoDeleteTasks: String
)
