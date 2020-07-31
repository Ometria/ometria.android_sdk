package com.android.ometriasdk.core.event

import java.util.*

/**
 * Created by cristiandregan
 * on 28/07/2020.
 */

open class BaseEvent(
    val creationDate: String = Date().toString(),
    val flushDate: String? = null,
    val isFlushed: Boolean = false,
    val isAutomaticallyTracked: Boolean? = false
)