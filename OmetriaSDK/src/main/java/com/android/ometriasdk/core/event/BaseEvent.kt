package com.android.ometriasdk.core.event

import com.android.ometriasdk.BuildConfig
import java.util.*

/**
 * Created by cristiandregan
 * on 24/07/2020.
 */

open class BaseEvent(
    // ToDo Decide on how to manage time zone
    private val applicationID: String? = null,
    private val installmentID: String? = null,
    private val applicationVersion: String? = null,
    private val buildNumber: String? = null,
    private val sdkVersion: String? = BuildConfig.VERSION_NAME,
    private val platform: String = "Android",
    private val creationDate: Long = Date().time,
    private val flushDate: Long = Date().time,
    private val isFlushed: Boolean = false,
    private val isAutomaticallyTracked: Boolean? = false
)