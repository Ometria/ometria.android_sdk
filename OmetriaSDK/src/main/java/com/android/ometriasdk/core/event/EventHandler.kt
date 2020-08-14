package com.android.ometriasdk.core.event

import android.content.Context
import androidx.core.content.pm.PackageInfoCompat
import com.android.ometriasdk.core.Constants
import com.android.ometriasdk.core.Logger
import com.android.ometriasdk.core.Repository
import com.android.ometriasdk.core.network.ApiCallback
import com.android.ometriasdk.core.network.model.OmetriaApiResponse
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by cristiandregan
 * on 27/07/2020.
 */

private val TAG = EventHandler::class.simpleName
private const val BATCH_LIMIT = 2

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
        val installationId = repository.getInstallmentID()

        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val appVersion = packageInfo.versionName
        val appBuildNumber = PackageInfoCompat.getLongVersionCode(packageInfo).toString()

        val event = OmetriaEvent(
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

    private fun sendEvent(cachedEvent: OmetriaEvent) {
        repository.saveEvent(cachedEvent)
        flushEvents()
        Logger.d(TAG, "Track event: ", cachedEvent)
        writeEventToFile(cachedEvent)
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

    private fun flushEvents() {
        val eventsSet = repository.getEvents() ?: return

        if (shouldFlush(eventsSet)) {
            val eventList = eventsSet.toOmetriaEventList()
            eventList.groupBy { it.batchIdentifier() }.forEach { flush(it.value) }
        }
    }

    private fun flush(eventList: List<OmetriaEvent>) {
        val apiRequest = eventList.toApiRequest()

        repository.postEventsValidate(
            apiRequest,
            object : ApiCallback<OmetriaApiResponse> {
                override fun onSuccess(response: OmetriaApiResponse) {
                    Logger.d(TAG, "Successfully flushed")
                }

                override fun onError(error: String?) {
                    Logger.d(TAG, error ?: "")
                }
            })
    }

    private fun shouldFlush(eventsSet: Set<String>): Boolean {
        return eventsSet.isNotEmpty() && eventsSet.size >= BATCH_LIMIT
    }
}