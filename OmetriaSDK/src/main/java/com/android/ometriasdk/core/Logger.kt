package com.android.ometriasdk.core

import android.util.Log

const val VERBOSE = 2
const val DEBUG = 3
const val INFO = 4
const val WARN = 5
const val ERROR = 6

@Suppress("unused")
internal object Logger {

    private var minLevel: Int = WARN

    fun setLevel(minLevel: Int) {
        this.minLevel = minLevel
    }

    private fun createTag(tag: String): String {
        return "OmetriaSDK $tag"
    }

    fun v(tag: String, message: String) {
        if (shouldLog(VERBOSE)) {
            Log.v(createTag(tag), message)
        }
    }

    fun v(tag: String, message: String, throwable: Throwable) {
        if (shouldLog(VERBOSE)) {
            Log.v(createTag(tag), message, throwable)
        }
    }

    fun d(tag: String, message: String) {
        if (shouldLog(DEBUG)) {
            Log.d(createTag(tag), message)
        }
    }

    fun d(tag: String, message: String, any: Any) {
        if (shouldLog(DEBUG)) {
            Log.d(createTag(tag), "$message $any")
        }
    }

    fun d(tag: String, message: String, throwable: Throwable) {
        if (shouldLog(DEBUG)) {
            Log.d(createTag(tag), message, throwable)
        }
    }

    fun i(tag: String, message: String) {
        if (shouldLog(INFO)) {
            Log.i(createTag(tag), message)
        }
    }

    fun i(tag: String, message: String, throwable: Throwable) {
        if (shouldLog(INFO)) {
            Log.i(createTag(tag), message, throwable)
        }
    }

    fun w(tag: String, message: String) {
        if (shouldLog(WARN)) {
            Log.w(createTag(tag), message)
        }
    }

    fun w(tag: String, message: String, throwable: Throwable) {
        if (shouldLog(WARN)) {
            Log.w(createTag(tag), message, throwable)
        }
    }

    fun e(tag: String, message: String) {
        if (shouldLog(ERROR)) {
            Log.e(createTag(tag), message)
        }
    }

    fun e(tag: String, message: String?, throwable: Throwable?) {
        if (shouldLog(ERROR)) {
            Log.e(createTag(tag), message, throwable)
        }
    }

    fun wtf(tag: String, message: String) {
        if (shouldLog(ERROR)) {
            Log.wtf(createTag(tag), message)
        }
    }

    fun wtf(tag: String, message: String, throwable: Throwable) {
        if (shouldLog(ERROR)) {
            Log.wtf(createTag(tag), message, throwable)
        }
    }

    private fun shouldLog(level: Int): Boolean {
        return minLevel <= level
    }
}