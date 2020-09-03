package com.android.ometriasdk.core.event

import android.content.Context
import androidx.core.content.pm.PackageInfoCompat
import com.android.ometriasdk.core.Constants
import com.android.ometriasdk.core.Logger
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.Repository
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by cristiandregan
 * on 27/07/2020.
 */

private const val FLUSH_LIMIT = 20
private const val BATCH_LIMIT = 100
private const val THROTTLE_LIMIT = 10

internal class EventHandler(
    context: Context,
    private val repository: Repository
) {
    private val dateFormat: DateFormat =
        SimpleDateFormat(Constants.Date.API_DATE_FORMAT, Locale.getDefault())
    private val appId = context.packageName
    private val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    private var throttleCalendar = Calendar.getInstance()

    fun processEvent(
        type: OmetriaEventType,
        data: Map<String, Any>? = null
    ) {
        val installationId = repository.getInstallationId()
        val appVersion = packageInfo.versionName
        val appBuildNumber = PackageInfoCompat.getLongVersionCode(packageInfo).toString()

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

        when (event.type) {
            OmetriaEventType.PUSH_TOKEN_REFRESHED.id -> {
                data?.let {
                    repository.savePushToken(it[Constants.Params.PUSH_TOKEN] as String)
                }
            }
            OmetriaEventType.PROFILE_IDENTIFIED.id ->
                Ometria.instance().trackPushTokenRefreshedEvent(repository.getPushToken())
            OmetriaEventType.PROFILE_DEIDENTIFIED.id ->
                Ometria.instance().generateInstallationId()
        }

        sendEvent(event)
    }

    private fun sendEvent(ometriaEvent: OmetriaEvent) {
        Logger.d(Constants.Logger.EVENTS, "Track event - ", ometriaEvent)

        repository.saveEvent(ometriaEvent)
        when (ometriaEvent.type) {
            OmetriaEventType.PUSH_TOKEN_REFRESHED.id,
            OmetriaEventType.APP_FOREGROUNDED.id,
            OmetriaEventType.APP_BACKGROUNDED.id -> flushEvents()
            else -> flushEventsIfNeeded()
        }
    }

    private fun flushEventsIfNeeded() {
        if (shouldFlush()) {
            throttleCalendar.timeInMillis = System.currentTimeMillis()
            throttleCalendar.add(Calendar.SECOND, THROTTLE_LIMIT)

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
                            Constants.Logger.EVENTS,
                            "Successfully flushed ${it.size} events"
                        )
                    }, error = {
                        Logger.d(
                            Constants.Logger.EVENTS,
                            "Failed to flush ${it.size} events"
                        )
                    })
                }
        }
    }

    private fun shouldFlush(): Boolean = repository.getEvents().size >= FLUSH_LIMIT
            && System.currentTimeMillis() >= throttleCalendar.timeInMillis
}