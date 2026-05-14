package com.example.gokulahealth

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val channelId = "vaccination_channel"

        val notificationManager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        // CREATE NOTIFICATION CHANNEL

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                channelId,
                "Vaccination Reminder",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannel(channel)
        }

        // BUILD NOTIFICATION

        val notification =
            NotificationCompat.Builder(context, channelId)

                .setContentTitle("Vaccination Reminder")
                .setContentText(
                    "Vaccination date reminder for cattle"
                )

                .setSmallIcon(android.R.drawable.ic_dialog_info)

                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )

                .build()

        notificationManager.notify(1, notification)
    }
}