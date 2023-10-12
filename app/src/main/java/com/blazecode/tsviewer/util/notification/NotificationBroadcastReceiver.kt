package com.blazecode.tsviewer.util.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import util.ClientsWorker

class NotificationBroadcastReceiver : BroadcastReceiver() {

    val ACTION_REFRESH = "refresh"

    override fun onReceive(context: Context, intent: Intent?) {
        //REFRESH
        if (intent?.action == ACTION_REFRESH) {
            var workManager: WorkManager = context.let { WorkManager.getInstance(context) }

            val data = Data.Builder()
            data.putBoolean("suppress_db", true)
            data.putBoolean("suppress_notification", true)

            val oneTimeclientWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<ClientsWorker>()
                .setInputData(data.build())
                .build()

            workManager.enqueue(oneTimeclientWorkRequest)
        }
    }
}