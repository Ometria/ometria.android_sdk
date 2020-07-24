package com.android.ometriasdk.core

import android.util.Log

/**
 * Created by cristiandregan
 * on 20/07/2020.
 */

const val VERBOSE = 2
const val DEBUG = 3
const val INFO = 4
const val WARN = 5
const val ERROR = 6

internal object Logger {

    private var minLevel: Int = WARN

    fun setLevel(minLevel: Int) {
        this.minLevel = minLevel
    }

    fun v(tag: String, message: String) {
        if (shouldLog(VERBOSE)) {
            Log.v(tag, message)
        }
    }

    fun v(tag: String, message: String, throwable: Throwable) {
        if (shouldLog(VERBOSE)) {
            Log.v(tag, message, throwable)
        }
    }

    fun d(tag: String?, message: String) {
        if (shouldLog(DEBUG)) {
            Log.d(tag, message)
        }
    }

    fun d(tag: String?, message: String, any: Any) {
        if (shouldLog(DEBUG)) {
            Log.d(tag, "$message $any")
        }
    }

    fun d(tag: String, message: String, throwable: Throwable) {
        if (shouldLog(DEBUG)) {
            Log.d(tag, message, throwable)
        }
    }

    fun i(tag: String, message: String) {
        if (shouldLog(INFO)) {
            Log.i(tag, message)
        }
    }

    fun i(tag: String, message: String, throwable: Throwable) {
        if (shouldLog(INFO)) {
            Log.i(tag, message, throwable)
        }
    }

    fun w(tag: String, message: String) {
        if (shouldLog(WARN)) {
            Log.w(tag, message)
        }
    }

    fun w(tag: String, message: String, throwable: Throwable) {
        if (shouldLog(WARN)) {
            Log.w(tag, message, throwable)
        }
    }

    fun e(tag: String, message: String) {
        if (shouldLog(ERROR)) {
            Log.e(tag, message)
        }
    }

    fun e(tag: String, message: String, throwable: Throwable) {
        if (shouldLog(ERROR)) {
            Log.e(tag, message, throwable)
        }
    }

    fun wtf(tag: String, message: String) {
        if (shouldLog(ERROR)) {
            Log.wtf(tag, message)
        }
    }

    fun wtf(tag: String, message: String, throwable: Throwable) {
        if (shouldLog(ERROR)) {
            Log.wtf(tag, message, throwable)
        }
    }

    private fun shouldLog(level: Int): Boolean {
        return minLevel <= level
    }
}