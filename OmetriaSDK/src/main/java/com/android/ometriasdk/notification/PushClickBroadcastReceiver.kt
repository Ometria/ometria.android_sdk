package com.android.ometriasdk.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.webkit.URLUtil
import com.android.ometriasdk.core.Constants
import com.android.ometriasdk.core.Logger
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.event.OmetriaEventType
import com.android.ometriasdk.core.network.toMap
import org.json.JSONObject

/**
 * Created by cristiandregan
 * on 30/07/2020.
 */

internal const val OMETRIA_CONTEXT_KEY = "key_ometria_context"
const val NOTIFICATION_ACTION_URL_KEY = "deep_link_action_url_key"

/**
 * Used by Push notification's routing intent.
 * The purpose of using a BroadcastReceiver is to intercept [OmetriaEventType.NOTIFICATION_INTERACTED] event.
 */
internal class PushClickBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action != null && action == PUSH_TAP_ACTION && context != null) {
            val deepLinkActionUrl = intent.getStringExtra(NOTIFICATION_ACTION_URL_KEY)
            if (deepLinkActionUrl != null && URLUtil.isValidUrl(deepLinkActionUrl)) {
                Ometria.instance().notificationInteractionHandler.onDeepLinkInteraction(
                    deepLinkActionUrl
                )
            } else {
                val launcherIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                launcherIntent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(launcherIntent)
            }

            val ometriaContextString = intent.getStringExtra(OMETRIA_CONTEXT_KEY)
            ometriaContextString?.let {
                Ometria.instance().trackNotificationInteractedEvent(JSONObject(it).toMap())
            }
        }
    }
}