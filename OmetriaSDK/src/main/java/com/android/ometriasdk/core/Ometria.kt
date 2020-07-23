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

class Ometria private constructor() {

    internal lateinit var appConfig: AppConfig
    private var isInitialized = false

    /**
     * Kotlin Object ensures thread safety.
     */
    private object HOLDER {
        val INSTANCE = Ometria()
    }

    companion object {
        private val instance: Ometria by lazy { HOLDER.INSTANCE }

        @JvmStatic
        fun initialize(application: Application, apiKey: String, notificationIcon: Int): Ometria {
            val activityLifecycleHelper = OmetriaActivityLifecycleHelper()
            application.registerActivityLifecycleCallbacks(activityLifecycleHelper)

            val lifecycle = ProcessLifecycleOwner.get().lifecycle
            lifecycle.addObserver(activityLifecycleHelper)

            return instance.also {
                it.appConfig = AppConfig(application, apiKey, notificationIcon)
                it.isInitialized = true
            }
        }

        @JvmStatic
        fun instance(): Ometria {
            if (!instance.isInitialized) {
                throw IllegalStateException("SDK not initialized. Please initialize before using this method.")
            }

            return instance
        }
    }

    fun enableDebugging(enableDebugging: Boolean): Ometria {
        appConfig.enableDebugging = enableDebugging
        OmetriaLog.setLevel(VERBOSE)

        return instance
    }

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

    fun trackEvent(type: String, value: String, block: (Event).() -> Unit) {
        val event = Event(type, value).apply(block)

        OmetriaLog.d(TAG, "Track event", event)
    }

    fun trackEvent(event: Event) {

        OmetriaLog.d(TAG, "Track event", event)
    }
}