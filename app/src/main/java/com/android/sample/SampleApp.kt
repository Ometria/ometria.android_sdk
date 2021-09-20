package com.android.sample

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.notification.OmetriaNotification
import com.android.ometriasdk.notification.OmetriaNotificationInteractionHandler

/**
 * Created by cristiandregan
 * on 08/07/2020.
 */

class SampleApp : Application(), OmetriaNotificationInteractionHandler {
    companion object {
        lateinit var instance: SampleApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initializing Ometria SDK with application context, api token and notifications icon resource id
        // Note: Replace api token with your own
        Ometria.initialize(
            this,
            "YOUR_API_TOKEN",
            R.drawable.ic_notification_nys,
            ContextCompat.getColor(this, R.color.colorAccent)
        ).loggingEnabled(true)

        // Set the notificationInteractionDelegate in order to provide actions for
        // notifications that contain a deepLink URL.
        // The default functionality when you don't assign a delegate is opening urls in a browser
        Ometria.instance().notificationInteractionHandler = this
    }

    override fun onNotificationInteraction(ometriaNotification: OmetriaNotification) {
        Log.d(SampleApp::class.java.simpleName, "Open URL: ${ometriaNotification.deepLink}")
        Ometria.instance().trackDeepLinkOpenedEvent(ometriaNotification.deepLink, "Browser")

        openBrowser(ometriaNotification.deepLink)
    }

    private fun openBrowser(deepLink: String?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.data = Uri.parse(deepLink)
        startActivity(intent)
    }
}