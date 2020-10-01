package com.android.ometriasdk.core.event

import android.content.Context
import androidx.core.content.pm.PackageInfoCompat
import com.android.ometriasdk.core.Constants
import com.android.ometriasdk.core.Constants.Logger.EVENTS
import com.android.ometriasdk.core.Constants.Logger.NETWORK
import com.android.ometriasdk.core.Constants.Params.CUSTOMER_ID
import com.android.ometriasdk.core.Constants.Params.EMAIL
import com.android.ometriasdk.core.Logger
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.Repository
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by cristiandregan
 * on 27/07/2020.
 */

private const val FLUSH_LIMIT = 10
private const val BATCH_LIMIT = 100
private const val THROTTLE_LIMIT = 10L
private const val NO_VALUE = -1L

/**
 * Class used to process logged or intercepted events.
 */
internal class EventHandler(context: Context, private val repository: Repository) {
    private val dateFormat: DateFormat =
        SimpleDateFormat(Constants.Date.API_DATE_FORMAT, Locale.getDefault())
    private val appId = context.packageName
    private val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    private var syncTimestamp: Long = NO_VALUE

    fun processEvent(
        type: OmetriaEventType,
        data: MutableMap<String, Any>? = null
    ) {
        val installationId = repository.getInstallationId()
        val appVersion = packageInfo.versionName
        val appBuildNumber = PackageInfoCompat.getLongVersionCode(packageInfo).toString()

        if (type == OmetriaEventType.PUSH_TOKEN_REFRESHED) {
            data?.let {
                repository.savePushToken(it[Constants.Params.PUSH_TOKEN] as String)

                repository.getCustomerId()?.let { customerId ->
                    data[CUSTOMER_ID] = customerId
                }

                repository.getEmail()?.let { customerId ->
                    data[EMAIL] = customerId
                }
            }
        }

        val event = OmetriaEvent(
            eventId = UUID.randomUUID().toString(),
            dtOccurred = dateFormat.format(Calendar.getInstance().time),
            appId = appId,
            installationId = installationId,
            appVersion = appVersion,
            appBuildNumber = appBuildNumber,
            type = type.id,
            data = data
        )

        sendEvent(event)

        when (event.type) {
            OmetriaEventType.PROFILE_IDENTIFIED.id -> {
                cacheProfileIdentifiedData(data)
                Ometria.instance().trackPushTokenRefreshedEvent(repository.getPushToken())
            }
            OmetriaEventType.PROFILE_DEIDENTIFIED.id -> {
                Ometria.instance().generateInstallationId()
                repository.clearProfileIdentifiedData()
            }
        }
    }

    private fun cacheProfileIdentifiedData(data: Map<String, Any>?) {
        data?.let {
            if (it[CUSTOMER_ID] != null) {
                repository.saveCustomerId(it[CUSTOMER_ID] as String)
            } else {
                repository.saveEmail(it[EMAIL] as String)
            }
        }
    }

    private fun sendEvent(ometriaEvent: OmetriaEvent) {
        Logger.d(EVENTS, "Track event - ", ometriaEvent)

        repository.saveEvent(ometriaEvent)
        when (ometriaEvent.type) {
            OmetriaEventType.PUSH_TOKEN_REFRESHED.id,
            OmetriaEventType.APP_FOREGROUNDED.id,
            OmetriaEventType.APP_BACKGROUNDED.id -> flushEvents()
            else -> flushEventsIfNeeded()
        }
    }

    private fun flushEventsIfNeeded() {
        if (shouldFlush() && canFlush()) {
            syncTimestamp = System.currentTimeMillis()
            flushEvents()
        }
    }

    fun flushEvents() {
        val events = repository.getEvents().filter { !it.isBeingFlushed }

        events.groupBy { it.batchIdentifier() }.forEach { group ->
            group.value
                .chunked(BATCH_LIMIT)
                .forEach {
                    repository.flushEvents(it, success = {
                        Logger.d(
                            EVENTS,
                            "Successfully flushed ${it.size} events"
                        )
                    }, error = {
                        Logger.d(
                            EVENTS,
                            "Failed to flush ${it.size} events"
                        )
                    })
                }
        }
    }

    /**
     * Checks if enough time passed between two consecutive flushes
     */
    private fun canFlush(): Boolean {
        return (System.currentTimeMillis() >= syncTimestamp + TimeUnit.SECONDS.toMillis(
            THROTTLE_LIMIT
        )).also {
            if (!it) {
                Logger.d(
                    NETWORK,
                    "Attempted to flush events but not enough time has passed since the last flush."
                )
            }
        }
    }

    /**
     * Checks if the size of cached events list, which are not currently being flushed, is greater than
     * the [FLUSH_LIMIT]
     */
    private fun shouldFlush(): Boolean =
        repository.getEvents().filter { !it.isBeingFlushed }.size >= FLUSH_LIMIT
}