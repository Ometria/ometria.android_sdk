package com.android.sample

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.notification.OmetriaNotification
import com.android.ometriasdk.notification.OmetriaNotificationInteractionHandler
import com.android.sample.data.AppPreferencesUtils

class SampleApp : Application(), OmetriaNotificationInteractionHandler {

    companion object {
        lateinit var instance: SampleApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Update security provider to protect against SSL exploits section
        val workRequest = OneTimeWorkRequestBuilder<PatchWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        WorkManager.getInstance(this).enqueue(workRequest)

        // Initializing Ometria SDK with application context, api token and notifications icon resource id
        // Note: Replace api token with your own
        Ometria.initialize(
            application = this,
            apiToken = AppPreferencesUtils.getApiToken() ?: "YOUR_API_TOKEN",
            notificationIcon = R.drawable.ic_notification_nys,
            notificationColor = ContextCompat.getColor(this, R.color.colorAccent),
            notificationChannelName = "Custom Channel Name"
        ).loggingEnabled(true)

        // Set the notificationInteractionDelegate in order to provide actions for
        // notifications that contain a deepLink URL.
        // The default functionality when you don't assign a delegate is asking the OS for an app that can open the url
        Ometria.instance().notificationInteractionHandler = this
    }

    override fun onNotificationInteraction(ometriaNotification: OmetriaNotification) {
        ometriaNotification.deepLinkActionUrl?.let { safeDeeplinkActionUrl ->
            val intent = Intent(Intent.ACTION_VIEW).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                data = safeDeeplinkActionUrl.toUri()
            }

            try {
                startActivity(intent)
            } catch (_: ActivityNotFoundException) {
                return
            }
        }
    }
}