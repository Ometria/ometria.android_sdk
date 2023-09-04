package com.android.ometriasdk.core

import android.content.SharedPreferences

internal fun SharedPreferences.copyTo(dest: SharedPreferences) {
    for (entry in all.entries) {
        val key = entry.key
        val value: Any? = entry.value
        dest.set(key, value)
    }
}

internal inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
    val editor = this.edit()
    operation(editor)
    editor.apply()
}

internal fun SharedPreferences.set(key: String, value: Any?) {
    when (value) {
        is String? -> edit { it.putString(key, value) }
        is Int -> edit { it.putInt(key, value.toInt()) }
        is Boolean -> edit { it.putBoolean(key, value) }
        is Float -> edit { it.putFloat(key, value.toFloat()) }
        is Long -> edit { it.putLong(key, value.toLong()) }
        else -> Unit
    }
}

internal fun SharedPreferences.clear() {
    edit { it.clear() }
}