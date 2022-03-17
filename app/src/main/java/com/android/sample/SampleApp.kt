package com.android.sample

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.URLUtil
import androidx.core.content.ContextCompat
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.notification.OmetriaNotification
import com.android.ometriasdk.notification.OmetriaNotificationInteractionHandler
import com.android.sample.data.AppPreferencesUtils
import com.android.sample.presentation.MainActivity
import com.android.sample.presentation.OMETRIA_NOTIFICATION_STRING_EXTRA_KEY

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
            AppPreferencesUtils.getApiToken() ?: "YOUR_API_TOKEN",
            R.drawable.ic_notification_nys,
            ContextCompat.getColor(this, R.color.colorAccent)
        ).loggingEnabled(true)

        // Set the notificationInteractionDelegate in order to provide actions for
        // notifications that contain a deepLink URL.
        // The default functionality when you don't assign a delegate is opening urls in a browser
        Ometria.instance().notificationInteractionHandler = this
    }

    override fun onNotificationInteraction(ometriaNotification: OmetriaNotification) {
        openMainActivity(ometriaNotification.toString())
        openBrowser(ometriaNotification.deepLinkActionUrl)
    }

    private fun openMainActivity(ometriaNotificationString: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(OMETRIA_NOTIFICATION_STRING_EXTRA_KEY, ometriaNotificationString)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun openBrowser(deepLink: String?) {
        deepLink?.let { safeDeepLink ->
            if (URLUtil.isValidUrl(safeDeepLink).not()) return

            Ometria.instance()
                .trackDeepLinkOpenedEvent(safeDeepLink, "Browser")
            Log.d(SampleApp::class.java.simpleName, "Open URL: $safeDeepLink")
            val intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.data = Uri.parse(safeDeepLink)
            startActivity(intent)
        }
    }
}