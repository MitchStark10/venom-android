package com.venom.venomtasks.classes

enum class AutoDeleteOptions(val value: String) {
    NEVER("-1"),
    ONE_WEEK("7"),
    TWO_WEEKS("14"),
    ONE_MONTH("30")
}

data class SettingsResponse(
    val email: String,
    var autoDeleteTasks: String,
    var dailyReportIgnoreWeekends: Boolean
)

data class EditSettingsBody(
    val autoDeleteTasks: String,
    val dailyReportIgnoreWeekends: Boolean
)
