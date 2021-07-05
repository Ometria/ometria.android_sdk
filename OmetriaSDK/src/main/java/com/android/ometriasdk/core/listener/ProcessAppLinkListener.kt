package com.android.ometriasdk.core.listener

/**
 * A callback interface for processing an App Link URL.
 * Processing is done async. Results will be returned on Main Thread.
 */
interface ProcessAppLinkListener {
    fun onProcessResult(redirectUrl: String)
    fun onProcessFailed(error: String)
}