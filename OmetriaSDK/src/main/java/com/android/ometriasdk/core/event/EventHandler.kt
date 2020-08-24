package com.android.ometriasdk.core.event

import android.content.Context
import androidx.core.content.pm.PackageInfoCompat
import com.android.ometriasdk.core.Constants
import com.android.ometriasdk.core.Logger
import com.android.ometriasdk.core.Repository
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by cristiandregan
 * on 27/07/2020.
 */

private const val BATCH_LIMIT = 20

internal class EventHandler(
    private val context: Context,
    private val repository: Repository
) {

    fun processEvent(
        type: OmetriaEventType,
        data: Map<String, Any>? = null
    ) {
        val dateFormat: DateFormat =
            SimpleDateFormat(Constants.Date.API_DATE_FORMAT, Locale.getDefault())

        val appId = context.packageName
        val installationId = repository.getInstallationId()

        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val appVersion = packageInfo.versionName
        val appBuildNumber = PackageInfoCompat.getLongVersionCode(packageInfo).toString()

        val event = OmetriaEvent(
            eventId = UUID.randomUUID().toString(),
            timestampOccurred = dateFormat.format(Calendar.getInstance().time),
            appId = appId,
            installationId = installationId,
            appVersion = appVersion,
            appBuildNumber = appBuildNumber,
            type = type.id,
            data = data
        )

        sendEvent(event)
    }

    private fun sendEvent(ometriaEvent: OmetriaEvent) {
        Logger.d(Constants.Logger.EVENTS, "Track event - ", ometriaEvent)

        repository.saveEvent(ometriaEvent)
        flushEventsIfNeeded()
        writeEventToFile(ometriaEvent)
    }

    private fun writeEventToFile(event: OmetriaEvent) {
        val path = context.getExternalFilesDir(null)

        val letDirectory = File(path, "Events")
        letDirectory.mkdirs()
        val file = File(letDirectory, "Events.txt")

        FileOutputStream(file, true).use {
            it.write("- $event\n".toByteArray())
        }
    }

    private fun flushEventsIfNeeded() {
        val events = repository.getEvents().filter { !it.isBeingFlushed }

        if (shouldFlush(events)) {
            events.groupBy { it.batchIdentifier() }.forEach { repository.flushEvents(it.value) }
        }
    }

    private fun shouldFlush(events: List<OmetriaEvent>): Boolean =
        events.isNotEmpty() && events.size >= BATCH_LIMIT

}