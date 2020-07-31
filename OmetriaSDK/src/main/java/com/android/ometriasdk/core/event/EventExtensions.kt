package com.android.ometriasdk.core.event

import android.content.Context
import androidx.core.content.pm.PackageInfoCompat
import com.android.ometriasdk.core.LocalCache

/**
 * Created by cristiandregan
 * on 28/07/2020.
 */

internal fun Event.toGeneric(
    context: Context,
    localCache: LocalCache
): GenericEvent {
    val applicationID = context.packageName
    val installmentID = localCache.getInstallmentID()

    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val applicationVersion = packageInfo.versionName
    val buildNumber = PackageInfoCompat.getLongVersionCode(packageInfo).toString()

    return GenericEvent(
        this,
        applicationID = applicationID,
        installmentID = installmentID,
        applicationVersion = applicationVersion,
        buildNumber = buildNumber
    )
}