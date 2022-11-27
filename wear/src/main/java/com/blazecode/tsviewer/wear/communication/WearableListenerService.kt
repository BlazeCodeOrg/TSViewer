/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

package com.blazecode.tsviewer.wear.communication

import com.blazecode.tsviewer.wear.complication.ComplicationDataHolder
import com.blazecode.tsviewer.wear.complication.ComplicationProvider
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class WearableListenerService: WearableListenerService() {

    companion object {
        const val CLIENTS_PATH = "/clients"
        private const val CLIENT_LIST_KEY = "clientlist"
        private const val TIME_MILLIS = "timeMillis"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)

        dataEvents.forEach { event ->
            event.dataItem.also { item ->
                if (item.uri.path!!.compareTo(CLIENTS_PATH) == 0) {
                    DataMapItem.fromDataItem(item).dataMap.apply {

                        // CLIENT LIST
                        val clientList = getStringArray(CLIENT_LIST_KEY)

                        if(!clientList.isNullOrEmpty()){
                            ComplicationDataHolder.list = clientList.toMutableList()
                            ComplicationProvider().update(this@WearableListenerService)
                        } else {
                            ComplicationDataHolder.list = mutableListOf()
                            ComplicationProvider().update(this@WearableListenerService)
                        }

                        // TIME STAMP
                        val timeMillis = getLong(TIME_MILLIS)
                        ComplicationDataHolder.time = timeMillis
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}