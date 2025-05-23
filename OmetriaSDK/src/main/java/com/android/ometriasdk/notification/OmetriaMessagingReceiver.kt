package com.android.ometriasdk.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.android.ometriasdk.core.Logger
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.Ometria.Companion.clearOldInstance
import com.android.ometriasdk.core.event.OmetriaEventType
import com.android.ometriasdk.core.network.toOmetriaNotificationBody
import com.google.firebase.messaging.RemoteMessage

/**
 * Used as Push notification interceptor.
 * This will not replace Firebase's own BroadcastReceiver.
 * The purpose of using this BroadcastReceive is to log [OmetriaEventType.NOTIFICATION_RECEIVED] event
 * when the app is killed for React Native use-case.
 */
class OmetriaMessagingReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        val remoteMessage = RemoteMessage(intent?.extras)

        try {
            Ometria.instance()
        } catch (_: IllegalStateException) {
            Ometria.initializeForInternalUsage(context)
            try {
                if (Ometria.instance().isReactNativeUsage()) {
                    val ometriaNotificationBody = remoteMessage.toOmetriaNotificationBody()
                    val ometriaNotificationContext = ometriaNotificationBody?.context

                    ometriaNotificationContext?.let {
                        Ometria.instance().trackNotificationReceivedEvent(it)
                        clearOldInstance()
                    }
                }
            } catch (_: IllegalStateException) {
                // missing API token, ignore
                Logger.e(tag = "OmetriaMessagingReceiver", message = "Missing API token", throwable = null)
            }
        }
    }
}