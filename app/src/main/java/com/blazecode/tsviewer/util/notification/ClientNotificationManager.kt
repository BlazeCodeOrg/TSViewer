package com.blazecode.tsviewer.util.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.blazecode.tsviewer.MainActivity
import com.blazecode.tsviewer.R

class ClientNotificationManager(private val context: Context) {

    fun post(clientListNames: MutableList<String>){

        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val notificationPendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (clientListNames.size > 0) {
            val builder = NotificationCompat.Builder(context, context.getString(R.string.notificationChannelClientID))
                .setGroup(context.getString(R.string.notificationChannelClientID))
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentText(clientListNames.joinToString())
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentIntent(notificationPendingIntent)

            if (clientListNames.size == 1) builder.setContentTitle("${clientListNames.size} ${context.resources.getString(R.string.client_connected)}")
            else builder.setContentTitle("${clientListNames.size} ${context.resources.getString(R.string.clients_connected)}")

            with(NotificationManagerCompat.from(context)) {
                notify(1, builder.build())
            }
        } else {
            removeNotification()
        }
    }

    fun createChannel(){
        val name = context.getString(R.string.notificationChannelClient)
        val importance = NotificationManager.IMPORTANCE_MIN
        val channel = NotificationChannel(context.getString(R.string.notificationChannelClientID), name, importance).apply{}
        // Register the channel with the system
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun removeNotification(){
        NotificationManagerCompat.from(context).cancel(null, 1)
    }
}