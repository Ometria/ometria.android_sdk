package com.android.ometriasdk.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.android.ometriasdk.core.Constants.Logger.PUSH_NOTIFICATIONS
import com.android.ometriasdk.core.Logger
import com.android.ometriasdk.core.network.OmetriaThreadPoolExecutor
import java.io.IOException
import java.net.URL

/**
 * Created by cristiandregan
 * on 16/07/2020.
 */

const val PUSH_TAP_ACTION = "com.android.ometriasdk.push_notification_tap"
const val OMETRIA_CHANNEL_ID = "ometria"
const val OMETRIA_CHANNEL_NAME = "ometria"

internal class OmetriaPushNotification(
    private val context: Context,
    private val notificationIcon: Int,
    private val executor: OmetriaThreadPoolExecutor
) {

    fun createPushNotification(
        title: String?,
        body: String?,
        ometriaNotification: OmetriaNotification?,
        collapseId: String?
    ) {
        if (ometriaNotification?.imageUrl != null) {
            displayNotificationWithImage(title, body, ometriaNotification.imageUrl, collapseId)
        } else {
            displayNotification(title = title, body = body, collapseId = collapseId)
        }
    }

    private fun displayNotification(
        title: String?,
        body: String?,
        largeIcon: Bitmap? = null,
        collapseId: String?
    ) {
        val contentIntent = PendingIntent.getBroadcast(
            context,
            System.currentTimeMillis().toInt(),
            getRoutingIntent(),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(context, OMETRIA_CHANNEL_ID)
            .setSmallIcon(notificationIcon)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .setGroup(collapseId)
            .setLargeIcon(largeIcon)

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

    private fun getRoutingIntent(): Intent {
        val options = Bundle()

        return Intent().setAction(PUSH_TAP_ACTION)
            .setClass(
                context,
                PushClickBroadcastReceiver::class.java
            ).putExtras(options)
    }

    private fun displayNotificationWithImage(
        title: String?,
        body: String?,
        stringUrl: String?,
        collapseId: String?
    ) {
        executor.execute {
            val url = URL(stringUrl)
            try {
                BitmapFactory.decodeStream(url.openConnection().getInputStream())?.also { bitmap ->
                    displayNotification(title, body, bitmap, collapseId)
                } ?: run {
                    Logger.e(
                        PUSH_NOTIFICATIONS,
                        "The notification content has missing fields or is incorrectly formatted."
                    )
                }
            } catch (e: IOException) {
                Logger.e(PUSH_NOTIFICATIONS, e.message, e)
            }
        }
    }
}