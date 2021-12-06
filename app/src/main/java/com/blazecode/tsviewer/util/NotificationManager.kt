package com.blazecode.tsviewer.util

import androidx.core.app.NotificationCompat
import com.blazecode.tsviewer.R

class NotificationManager {

    fun post(){
        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(R.string.app_name)
            .setContentText()
            .setPriority(NotificationCompat.PRIORITY_LOW)
    }

    fun createChannel(){

    }
}