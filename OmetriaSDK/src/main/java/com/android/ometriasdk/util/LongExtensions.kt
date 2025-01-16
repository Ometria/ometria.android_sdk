package com.android.ometriasdk.util

fun Long.isOlderThanAWeek(): Boolean {
    val oneWeekInMillis = 7 * 24 * 60 * 60 * 1000L
    val currentTime = System.currentTimeMillis()
    return currentTime - this > oneWeekInMillis
}
