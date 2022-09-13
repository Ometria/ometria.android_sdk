package com.android.ometriasdk.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.event.OmetriaEventType
import com.android.ometriasdk.core.network.toOmetriaNotification
import com.android.ometriasdk.core.network.toOmetriaNotificationBody

internal const val OMETRIA_NOTIFICATION_BODY_KEY = "ometria_notification_body_key"

/**
 * Used by Push notification's routing intent.
 * The purpose of using a BroadcastReceiver is to intercept [OmetriaEventType.NOTIFICATION_INTERACTED] event.
 */
internal class PushClickBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action != null && action == PUSH_TAP_ACTION && context != null) {
            val ometriaNotificationBody =
                intent.getStringExtra(OMETRIA_NOTIFICATION_BODY_KEY)?.toOmetriaNotificationBody()

            ometriaNotificationBody?.let { safeOmetriaNotificationBody ->
                if (safeOmetriaNotificationBody.deepLinkActionUrl != null) {
                    Ometria.instance().notificationInteractionHandler.onDeepLinkInteraction(
                        safeOmetriaNotificationBody.deepLinkActionUrl
                    )
                }
                Ometria.instance().notificationInteractionHandler.onNotificationInteraction(
                    safeOmetriaNotificationBody.toOmetriaNotification()
                )

                safeOmetriaNotificationBody.context?.let {
                    Ometria.instance().trackNotificationInteractedEvent(it)
                }
            }
        }
    }
}