package com.udacity.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.udacity.DetailActivity
import com.udacity.R

class NotificationUtil {

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    Constants.CHANNEL_ID,
                    Constants.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )

            notificationChannel.description = "Description"

            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    fun makeANotification(context: Context, fileName: String, status: String) {

        val contentIntent = Intent(context, DetailActivity::class.java).apply {
            putExtra(Constants.FILE_NAME, fileName)
            putExtra(Constants.STATUS, status)
        }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            Constants.NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel =
                NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, importance)
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

            notificationManager?.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setContentTitle(if (status == Constants.STATUS_SUCCESS) "Udacity: Android Kotlin Nanodegree." else "ERROR.")
            .setContentText(if (status == Constants.STATUS_SUCCESS) "The project is download" else "Something wrong happened.")
            .addAction(0, "Check the status", contentPendingIntent)
            .setContentIntent(contentPendingIntent)

        NotificationManagerCompat.from(context).notify(Constants.NOTIFICATION_ID, builder.build())
    }

}