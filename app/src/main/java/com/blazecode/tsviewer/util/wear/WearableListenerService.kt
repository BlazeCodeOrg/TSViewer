/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

package com.blazecode.tsviewer.util.wear

import android.content.Intent
import com.blazecode.tsviewer.MainActivity
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService

class WearableListenerService: WearableListenerService() {

    companion object {
        const val LAUNCH_PATH = "/start-activity"
        private const val LAUNCH_KEY = "startActivity"
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)

        dataEvents.forEach { event ->
            event.dataItem.also { item ->
                if (item.uri.path!!.compareTo(LAUNCH_PATH) == 0) {
                    DataMapItem.fromDataItem(item).dataMap.apply {

                        this@WearableListenerService.startActivity(
                            Intent(this@WearableListenerService, MainActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                }
            }
        }
    }
}