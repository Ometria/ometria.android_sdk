package com.android.ometriasdk.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.network.toMap
import org.json.JSONObject

/**
 * Created by cristiandregan
 * on 30/07/2020.
 */

internal const val OMETRIA_CONTEXT_KEY = "key_ometria_context"
const val NOTIFICATION_ACTION_URL_KEY = "deep_link_action_url_key"

internal class PushClickBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action != null && action == PUSH_TAP_ACTION && context != null) {
            val mainIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            val extras = Bundle()
            extras.putString(
                NOTIFICATION_ACTION_URL_KEY,
                intent.getStringExtra(NOTIFICATION_ACTION_URL_KEY)
            )
            mainIntent?.putExtras(extras)
            mainIntent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(mainIntent)

            val ometriaContextString = intent.getStringExtra(OMETRIA_CONTEXT_KEY)
            ometriaContextString?.let {
                Ometria.instance().trackNotificationInteractedEvent(JSONObject(it).toMap())
            }
        }
    }
}