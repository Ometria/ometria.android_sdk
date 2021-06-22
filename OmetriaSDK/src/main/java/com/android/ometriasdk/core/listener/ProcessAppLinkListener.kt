package com.android.ometriasdk.core.listener

/**
 * A callback interface for processing an App Link URL.
 * Processing is done async. Results will be returned on Main Thread.
 */
interface ProcessAppLinkListener {

    /**
     * Called when a process completes successfully.
     * @param url The redirect URL resulted from process.
     */
    fun onProcessResult(url: String)

    /**
     * Called when an exception occurs during a process.
     * @param error The error message
     */
    fun onProcessFailed(error: String)
}