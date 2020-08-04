package com.android.ometriasdk.core

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.android.ometriasdk.core.event.Event
import com.android.ometriasdk.core.event.EventHandler
import com.android.ometriasdk.core.event.OmetriaEventType
import com.android.ometriasdk.lifecycle.OmetriaActivityLifecycleHelper
import com.android.ometriasdk.notification.NotificationHandler
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by cristiandregan
 * on 08/07/2020.
 */

private val TAG = Ometria::class.simpleName

class Ometria private constructor() {

    private lateinit var appConfig: AppConfig
    private var isInitialized = false
    private lateinit var localCache: LocalCache
    private lateinit var eventHandler: EventHandler

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
                it.eventHandler = EventHandler(application, it.localCache)

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
        NotificationHandler.showNotification(
            remoteMessage,
            appConfig.context,
            appConfig.notificationIcon
        )

        trackEvent(OmetriaEventType.RECEIVE_NOTIFICATION, remoteMessage.messageId)
    }

    fun onNewToken(token: String) {
        trackEvent(OmetriaEventType.REFRESH_PUSH_TOKEN, token)
    }

    fun trackEvent(event: Event) {
        eventHandler.processEvent(event)
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