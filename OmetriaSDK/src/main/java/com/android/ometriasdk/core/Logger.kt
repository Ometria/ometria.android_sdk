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

    private fun makeFinalTag(tag: String?): String {
        return "OmetriaSDK $tag"
    }

    fun v(tag: String, message: String) {
        if (shouldLog(VERBOSE)) {
            Log.v(makeFinalTag(tag), message)
        }
    }

    fun v(tag: String, message: String, throwable: Throwable) {
        if (shouldLog(VERBOSE)) {
            Log.v(makeFinalTag(tag), message, throwable)
        }
    }

    fun d(tag: String?, message: String) {
        if (shouldLog(DEBUG)) {
            Log.d(makeFinalTag(tag), message)
        }
    }

    fun d(tag: String?, message: String, any: Any) {
        if (shouldLog(DEBUG)) {
            Log.d(makeFinalTag(tag), "$message $any")
        }
    }

    fun d(tag: String, message: String, throwable: Throwable) {
        if (shouldLog(DEBUG)) {
            Log.d(makeFinalTag(tag), message, throwable)
        }
    }

    fun i(tag: String, message: String) {
        if (shouldLog(INFO)) {
            Log.i(makeFinalTag(tag), message)
        }
    }

    fun i(tag: String, message: String, throwable: Throwable) {
        if (shouldLog(INFO)) {
            Log.i(makeFinalTag(tag), message, throwable)
        }
    }

    fun w(tag: String, message: String) {
        if (shouldLog(WARN)) {
            Log.w(makeFinalTag(tag), message)
        }
    }

    fun w(tag: String, message: String, throwable: Throwable) {
        if (shouldLog(WARN)) {
            Log.w(makeFinalTag(tag), message, throwable)
        }
    }

    fun e(tag: String?, message: String) {
        if (shouldLog(ERROR)) {
            Log.e(makeFinalTag(tag), message)
        }
    }

    fun e(tag: String?, message: String?, throwable: Throwable?) {
        if (shouldLog(ERROR)) {
            Log.e(makeFinalTag(tag), message, throwable)
        }
    }

    fun wtf(tag: String, message: String) {
        if (shouldLog(ERROR)) {
            Log.wtf(makeFinalTag(tag), message)
        }
    }

    fun wtf(tag: String, message: String, throwable: Throwable) {
        if (shouldLog(ERROR)) {
            Log.wtf(makeFinalTag(tag), message, throwable)
        }
    }

    private fun shouldLog(level: Int): Boolean {
        return minLevel <= level
    }
}