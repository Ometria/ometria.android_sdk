package com.android.ometriasdk.core

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.android.ometriasdk.core.event.EventHandler
import com.android.ometriasdk.core.event.OmetriaEventType
import com.android.ometriasdk.core.network.Repository
import com.android.ometriasdk.core.network.RetrofitBuilder
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
    private lateinit var repository: Repository

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
                it.repository = Repository(RetrofitBuilder.getOmetriaApi(it.appConfig))
                it.eventHandler = EventHandler(application, it.localCache, it.repository)

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

        trackEvent(OmetriaEventType.NOTIFICATION_RECEIVED, remoteMessage.messageId)
    }

    fun onNewToken(token: String) {
        trackEvent(OmetriaEventType.PUSH_TOKEN_REFRESHED, token)
    }

    internal fun trackEvent(
        type: OmetriaEventType,
        data: Any? = null
    ) {
    }
}