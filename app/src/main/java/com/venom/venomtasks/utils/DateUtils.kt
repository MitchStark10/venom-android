package com.venom.venomtasks.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

/**
 * Return date in specified format.
 * @param milliSeconds Date in milliseconds
 * @param dateFormat Date format
 * @return String representing date in specified format
 */
@SuppressLint("SimpleDateFormat")
fun getDateStringFromMillis(
    milliSeconds: Long,
    dateFormat: String? = "MM/dd/yyyy",
    timezoneId: String? = "UTC"
): String {
    val date = Date(milliSeconds)
    val format = SimpleDateFormat(dateFormat)
    format.timeZone = TimeZone.getTimeZone(timezoneId)
    return format.format(date)
}

@SuppressLint("SimpleDateFormat")
fun getDateFromDateString(
    dateString: String?,
    dateFormat: String? = "MM/dd/yyyy",
    timezoneId: String? = "UTC"
): Date? {
    if (dateString.isNullOrEmpty()) {
        return null;
    }

    val formatter = SimpleDateFormat(dateFormat)
    formatter.timeZone = TimeZone.getTimeZone(timezoneId)
    return formatter.parse(dateString)
}