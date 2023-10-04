package com.android.ometriasdk.notification

import android.app.Activity
import android.os.Bundle
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.event.OmetriaEventType
import com.android.ometriasdk.core.network.toOmetriaNotification
import com.android.ometriasdk.core.network.toOmetriaNotificationBody

internal const val OMETRIA_NOTIFICATION_BODY_KEY = "ometria_notification_body_key"

/**
 * Used as Push notification destination.
 * The purpose of using this Activity is to intercept [OmetriaEventType.NOTIFICATION_INTERACTED] event.
 */
class NotificationInteractionActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        finish()
    }
}