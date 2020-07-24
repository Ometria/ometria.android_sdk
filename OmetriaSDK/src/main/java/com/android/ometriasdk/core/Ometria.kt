package com.android.ometriasdk.core

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.android.ometriasdk.core.event.Event
import com.android.ometriasdk.core.event.OmetriaEventType
import com.android.ometriasdk.lifecycle.OmetriaActivityLifecycleHelper
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
    internal lateinit var localCache: LocalCache

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

            return instance.also {
                it.appConfig = AppConfig(application, apiKey, notificationIcon)
                it.localCache = LocalCache(application)
                it.isInitialized = true

                val activityLifecycleHelper = OmetriaActivityLifecycleHelper(it.localCache)

                val lifecycle = ProcessLifecycleOwner.get().lifecycle
                lifecycle.addObserver(activityLifecycleHelper)

                application.registerActivityLifecycleCallbacks(activityLifecycleHelper)
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
        Logger.setLevel(VERBOSE)

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

    fun trackEvent(event: Event) {
        Logger.d(TAG, "Track event: ", event)


    }

    fun trackEvent(
        type: OmetriaEventType,
        value: String? = null,
        configurationBlock: ((Event).() -> Unit)? = null
    ) {
        val event = Event(type, value)

        if (configurationBlock != null) {
            event.apply(configurationBlock)
        }

        trackEvent(event)
    }

    fun trackCustomEvent(
        customEventType: String,
        value: String,
        configurationBlock: ((Event).() -> Unit)? = null
    ) {
        val eventType = OmetriaEventType.CUSTOM
        eventType.id = customEventType
        val event = Event(eventType, value)

        if (configurationBlock != null) {
            event.apply(configurationBlock)
        }

        trackEvent(event)
    }

    fun trackCustomEvent(event: Event, customEventType: String) {
        event.type.id = customEventType

        trackEvent(event)
    }
}