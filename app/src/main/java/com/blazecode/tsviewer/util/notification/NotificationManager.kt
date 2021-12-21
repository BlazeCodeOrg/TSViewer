package com.blazecode.tsviewer.util.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.blazecode.tsviewer.R

class NotificationManager(private val context: Context) {

    fun post(clientListNames: MutableList<String>){
        if (clientListNames.size > 0) {
            val builder = NotificationCompat.Builder(context, context.getString(R.string.notificationChannelClientID))
                .setSmallIcon(R.drawable.ic_teamspeak_icon)
                .setContentText(clientListNames.joinToString())
                .setPriority(NotificationCompat.PRIORITY_MIN)

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notificationChannelClient)
            val importance = NotificationManager.IMPORTANCE_MIN
            val channel = NotificationChannel(context.getString(R.string.notificationChannelClientID), name, importance).apply{}
            // Register the channel with the system
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun removeNotification(){
        NotificationManagerCompat.from(context).cancel(null, 1)
    }
}