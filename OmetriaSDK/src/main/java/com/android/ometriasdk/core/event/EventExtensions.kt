package com.android.ometriasdk.core.event

import com.android.ometriasdk.core.Constants
import com.android.ometriasdk.core.network.model.OmetriaApiRequest
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by cristiandregan
 * on 28/07/2020.
 */

internal fun OmetriaEvent.batchIdentifier(): Int {
    return (appId + appBuildNumber + appVersion + osVersion).hashCode()
}

internal fun List<OmetriaEvent>.toApiRequest(): OmetriaApiRequest {
    val dateFormat: DateFormat =
        SimpleDateFormat(Constants.Date.API_DATE_FORMAT, Locale.getDefault())
    val ometriaEvent = this.first()

    return OmetriaApiRequest(
        appId = ometriaEvent.appId,
        appVersion = ometriaEvent.appVersion,
        installationId = ometriaEvent.installationId,
        appBuildNumber = ometriaEvent.appBuildNumber,
        sdkVersion = ometriaEvent.sdkVersion,
        osVersion = ometriaEvent.osVersion,
        timestampSent = dateFormat.format(Calendar.getInstance().time),
        events = this
    )
}