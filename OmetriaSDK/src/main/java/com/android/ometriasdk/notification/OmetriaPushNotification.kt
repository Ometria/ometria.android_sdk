package com.android.ometriasdk.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat

/**
 * Created by cristiandregan
 * on 16/07/2020.
 */

const val PUSH_TAP_ACTION = "com.android.ometriasdk.push_notification_tap"
const val PUSH_DISMISS_ACTION = "com.android.ometriasdk.push_notification_dismissed"
const val OMETRIA_CHANNEL_ID = "ometria"
const val OMETRIA_CHANNEL_NAME = "ometria"

internal class OmetriaPushNotification(
    private val context: Context,
    private val notificationIcon: Int
) {

    fun createPushNotification(
        title: String?,
        body: String?,
        ometriaNotification: OmetriaNotification?
    ) {
        val contentIntent = PendingIntent.getBroadcast(
            context,
            System.currentTimeMillis().toInt(),
            getRoutingIntent(),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val deleteIntent = PendingIntent.getBroadcast(
            context,
            0,
            getDeleteIntent(),
            0
        )

        val notificationBuilder = NotificationCompat.Builder(context, OMETRIA_CHANNEL_ID)
            .setSmallIcon(notificationIcon)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .setDeleteIntent(deleteIntent)

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
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun getRoutingIntent(): Intent {
        val options = Bundle()

        return Intent().setAction(PUSH_TAP_ACTION)
            .setClass(
                context,
                PushClickBroadcastReceiver::class.java
            ).putExtras(options)
    }

    private fun getDeleteIntent(): Intent {
        val options = Bundle()
        return Intent().setAction(PUSH_DISMISS_ACTION)
            .setClass(
                context,
                PushDismissBroadcastReceiver::class.java
            ).putExtras(options)
    }
}