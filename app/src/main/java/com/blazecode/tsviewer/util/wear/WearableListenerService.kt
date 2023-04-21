/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.util.wear

import android.content.Intent
import com.blazecode.tsviewer.MainActivity
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class WearableListenerService: WearableListenerService() {

    companion object {
        const val LAUNCH_PATH = "/start-activity"
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        when (messageEvent.path) {
            LAUNCH_PATH -> {
                this@WearableListenerService.startActivity(
                    Intent(this@WearableListenerService, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }
    }
}