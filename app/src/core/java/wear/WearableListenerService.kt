/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package wear

import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.blazecode.tsviewer.MainActivity
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import util.ClientsWorker

class WearableListenerService: WearableListenerService() {

    companion object {
        private const val LAUNCH_PATH = "/start-activity"
        private const val REQUEST_REFRESH = "/request-refresh"
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        when (messageEvent.path) {
            LAUNCH_PATH -> {
                startActivity(this@WearableListenerService)
            }
            REQUEST_REFRESH -> {
                refreshRequest(this@WearableListenerService)
            }
        }
    }

    private fun startActivity(context: Context) {
        context.startActivity(
            Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    private fun refreshRequest(context: Context){
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