package com.android.ometriasdk.core.event

import com.android.ometriasdk.core.AppGson
import com.android.ometriasdk.core.Constants
import com.android.ometriasdk.core.network.model.OmetriaApiRequest
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by cristiandregan
 * on 28/07/2020.
 */

internal fun Set<String>.toOmetriaEventList(): List<OmetriaEvent> {
    val eventsList = mutableListOf<OmetriaEvent>()

    this.sorted().forEach { cachedEventString ->
        eventsList.add(
            AppGson.instance.fromJson(
                cachedEventString,
                OmetriaEvent::class.java
            )
        )
    }

    return eventsList
}

internal fun OmetriaEvent.batchIdentifier(): Int {
    return (appId + appBuildNumber + appVersion + osVersion).hashCode()
}

internal fun List<OmetriaEvent>.toApiRequest(): OmetriaApiRequest {
    val dateFormat: DateFormat =
        SimpleDateFormat(Constants.Date.API_DATE_FORMAT, Locale.getDefault())
    val ometriaEvent = this.first()

    // ToDo extract installationId from event when generated
    return OmetriaApiRequest(
        appId = ometriaEvent.appId,
        appVersion = ometriaEvent.appVersion,
        installationId = "c2dd8d0f-2dcc-41c3-a4ef-b0cf164db357",
        appBuildNumber = ometriaEvent.appBuildNumber,
        sdkVersion = ometriaEvent.sdkVersion,
        osVersion = ometriaEvent.osVersion,
        timestampSent = dateFormat.format(Calendar.getInstance().time),
        events = this
    )
}