package com.android.ometriasdk.core.event

import android.content.Context
import android.os.Build
import com.android.ometriasdk.BuildConfig
import com.android.ometriasdk.core.LocalCache
import java.util.*

/**
 * Created by cristiandregan
 * on 24/07/2020.
 */

open class BaseEvent(
    // ToDo Decide on how to manage time zone
    private var applicationID: String? = null,
    private var installmentID: String? = null,
    private var applicationVersion: String? = null,
    private var buildNumber: String? = null,
    private val sdkVersion: String? = BuildConfig.VERSION_NAME,
    private val platform: String = "Android",
    private val osVersion: String? = Build.VERSION.RELEASE,
    private val deviceManufacturer: String? = Build.MANUFACTURER,
    private val deviceModel: String? = Build.MODEL,
    private val creationDate: Long = Date().time,
    private val flushDate: Long = Date().time,
    private val isFlushed: Boolean = false,
    private val isAutomaticallyTracked: Boolean? = false
) {
    internal fun completeData(context: Context, localCache: LocalCache) {
        this.applicationID = context.packageName
        this.installmentID = localCache.getInstallmentID()

        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        this.applicationVersion = packageInfo.versionName
        this.buildNumber = packageInfo.longVersionCode.toString()
    }
}