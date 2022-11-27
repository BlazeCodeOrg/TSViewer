/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

package com.blazecode.tsviewer.wear.communication

import android.content.Context
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

class WearDataManager(context: Context) {

    private val dataClient by lazy { Wearable.getDataClient(context) }

    companion object {
        const val LAUNCH_PATH = "/start-activity"
        private const val LAUNCH_KEY = "startActivity"
    }

    fun launchAppOnPhone(key: Long) {
        val request = PutDataMapRequest.create(LAUNCH_PATH).apply {
            dataMap.putLong(LAUNCH_KEY, key)
        }
            .asPutDataRequest()
            .setUrgent()

        val result = dataClient.putDataItem(request)
    }
}