package com.android.ometriasdk.core

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.android.ometriasdk.notifications.NotificationsHandler
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by cristiandregan
 * on 08/07/2020.
 */

private val TAG = Ometria::class.simpleName

object Ometria {
    private lateinit var appConfig: AppConfig

    fun initialize(application: Application, apiKey: String, notificationIcon: Int) {
        val activityLifecycleHelper = OmetriaActivityLifecycleHelper()
        application.registerActivityLifecycleCallbacks(activityLifecycleHelper)

        val lifecycle = ProcessLifecycleOwner.get().lifecycle
        lifecycle.addObserver(activityLifecycleHelper)

        appConfig = AppConfig(application, notificationIcon)
    }

    // Notification events

    fun onMessageReceived(remoteMessage: RemoteMessage) {
        NotificationsHandler.showNotification(
            remoteMessage,
            appConfig.context,
            appConfig.notificationIcon
        )
    }

    fun onNewToken(token: String) {
        // TODO send token to the API
    }

    // Usage events

    fun trackScreenViewed() {

    }

    fun trackProductClicked() {

    }

    fun trackProductViewed() {

    }

    fun trackProductAdded() {

    }

    fun trackProductRemoved() {

    }

    fun trackCartViewed() {

    }

    fun trackCheckoutStarted() {

    }

    fun trackOrderCompleted() {

    }
}