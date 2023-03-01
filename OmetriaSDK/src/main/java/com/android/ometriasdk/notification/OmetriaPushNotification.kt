package com.android.ometriasdk.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.android.ometriasdk.core.network.toJson

const val OMETRIA_CHANNEL_ID = "ometria"
const val OMETRIA_CHANNEL_NAME = " "

internal class OmetriaPushNotification(
    private val context: Context,
    private val notificationIcon: Int,
    private val notificationColor: Int?,
    private val notificationChannelName: String
) {

    @SuppressLint("LaunchActivityFromNotification")
    fun createPushNotification(
        title: String?,
        body: String?,
        image: Bitmap? = null,
        ometriaNotificationBody: OmetriaNotificationBody?,
        collapseId: String?
    ) {
        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            getRoutingIntent(ometriaNotificationBody),
            getFlags()
        )

        val notificationBuilder = NotificationCompat.Builder(context, OMETRIA_CHANNEL_ID)
            .setSmallIcon(notificationIcon)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setLargeIcon(image)
            .setColor(notificationColor ?: Notification.COLOR_DEFAULT)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                OMETRIA_CHANNEL_ID, notificationChannelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = notificationBuilder.build()
        notificationManager.notify(collapseId.hashCode(), notification)
    }

    private fun getRoutingIntent(ometriaNotificationBody: OmetriaNotificationBody?): Intent {
        val options = Bundle()
        ometriaNotificationBody?.let {
            options.putString(OMETRIA_NOTIFICATION_BODY_KEY, it.toJson().toString())
        }

        return Intent(context, NotificationInteractionActivity::class.java)
            .putExtras(options)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }

    private fun getFlags(): Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }
}