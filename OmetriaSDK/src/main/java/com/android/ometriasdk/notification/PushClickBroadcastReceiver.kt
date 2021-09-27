package com.android.ometriasdk.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.webkit.URLUtil
import com.android.ometriasdk.core.Constants
import com.android.ometriasdk.core.Logger
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.event.OmetriaEventType
import com.android.ometriasdk.core.network.toOmetriaNotification
import com.android.ometriasdk.core.network.toOmetriaNotificationBody

/**
 * Created by cristiandregan
 * on 30/07/2020.
 */

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
                if (safeOmetriaNotificationBody.deepLinkActionUrl != null
                    && URLUtil.isValidUrl(safeOmetriaNotificationBody.deepLinkActionUrl)
                ) {
                    Ometria.instance().notificationInteractionHandler.onNotificationInteraction(
                        safeOmetriaNotificationBody.toOmetriaNotification()
                    )
                    Logger.d(
                        Constants.Logger.PUSH_NOTIFICATIONS,
                        "Ometria Notification: ",
                        safeOmetriaNotificationBody.toOmetriaNotification()
                    )
                } else {
                    val launcherIntent =
                        context.packageManager.getLaunchIntentForPackage(context.packageName)
                    launcherIntent?.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(launcherIntent)
                }

                safeOmetriaNotificationBody.context?.let {
                    Ometria.instance().trackNotificationInteractedEvent(it)
                }
            }
        }
    }
}