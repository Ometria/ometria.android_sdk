package com.android.ometriasdk.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.android.ometriasdk.core.network.dataToJson

/**
 * Created by cristiandregan
 * on 16/07/2020.
 */

const val PUSH_TAP_ACTION = "com.android.ometriasdk.push_notification_tap"
const val OMETRIA_CHANNEL_ID = "ometria"
const val OMETRIA_CHANNEL_NAME = "ometria"

internal class OmetriaPushNotification(
    private val context: Context,
    private val notificationIcon: Int
) {

    fun createPushNotification(
        title: String?,
        body: String?,
        image: Bitmap? = null,
        ometriaNotification: OmetriaNotification?,
        collapseId: String?
    ) {
        val contentIntent = PendingIntent.getBroadcast(
            context,
            System.currentTimeMillis().toInt(),
            getRoutingIntent(ometriaNotification),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(context, OMETRIA_CHANNEL_ID)
            .setSmallIcon(notificationIcon)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .setLargeIcon(image)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                OMETRIA_CHANNEL_ID, OMETRIA_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = notificationBuilder.build()
        notificationManager.notify(collapseId.hashCode(), notification)
    }

    private fun getRoutingIntent(ometriaNotification: OmetriaNotification?): Intent {
        val options = Bundle()
        ometriaNotification?.let {
            options.putString(NOTIFICATION_ACTION_URL_KEY, it.deepLinkActionUrl)
            it.context?.let { context ->
                options.putString(OMETRIA_CONTEXT_KEY, context.dataToJson().toString())
            }
        }

        return Intent()
            .setAction(PUSH_TAP_ACTION)
            .setClass(context, PushClickBroadcastReceiver::class.java)
            .putExtras(options)
    }
}