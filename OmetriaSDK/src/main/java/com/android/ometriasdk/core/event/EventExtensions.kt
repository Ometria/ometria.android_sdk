package com.android.ometriasdk.core.event

import com.android.ometriasdk.core.Constants
import com.android.ometriasdk.core.network.model.OmetriaApiRequest
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * @return A hashcode used to compare and group cached events in batches when performing flush.
 */
internal fun OmetriaEvent.batchIdentifier(): Int {
    return (appId + appBuildNumber + appVersion + osVersion + installationId).hashCode()
}

/**
 * Used to convert a list of events into an [OmetriaApiRequest] object, which is the model used when
 * performing flush.
 *
 * @return An [OmetriaApiRequest]
 */
internal fun List<OmetriaEvent>.toApiRequest(): OmetriaApiRequest {
    val dateFormat: DateFormat =
        SimpleDateFormat(Constants.Date.API_DATE_FORMAT, Locale.UK)
    val ometriaEvent = this.first()

    return OmetriaApiRequest(
        appId = ometriaEvent.appId,
        appVersion = ometriaEvent.appVersion,
        installationId = ometriaEvent.installationId,
        appBuildNumber = ometriaEvent.appBuildNumber,
        sdkVersion = ometriaEvent.sdkVersion,
        osVersion = ometriaEvent.osVersion,
        dtSent = dateFormat.format(Calendar.getInstance().time),
        sdkVersionRN = ometriaEvent.sdkVersionRN,
        events = this
    )
}