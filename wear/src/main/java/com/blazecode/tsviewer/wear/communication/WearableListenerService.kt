/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.wear.communication

import android.util.Log
import android.widget.Toast
import com.blazecode.tsviewer.wear.complication.ComplicationProvider
import com.blazecode.tsviewer.wear.data.DataHolder
import com.blazecode.tsviewer.wear.data.WearDataPackage
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WearableListenerService: WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        when (messageEvent.path) {
            CLIENTS_PATH -> {
                val gson = GsonBuilder().create()
                val data = gson.fromJson(String(messageEvent.data), WearDataPackage::class.java)

                GlobalScope.launch(Dispatchers.Main) {
                    DataHolder.time.value = data.timestamp
                    DataHolder.list.value = data.clients.toMutableList()
                    ComplicationProvider().update(this@WearableListenerService)
                }

                Log.i("WearableListenerService", "Received ${data.clients.size} clients")
            }
            SERVICE_STATUS_PATH -> {
                GlobalScope.launch(Dispatchers.Main) {
                    DataHolder.serviceStatus.value = String(messageEvent.data).toBoolean()
                    println(String(messageEvent.data).toBoolean())
                    ComplicationProvider().update(this@WearableListenerService)
                }
            }
            TOAST_PATH -> {
                Toast.makeText(this, String(messageEvent.data), Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val CLIENTS_PATH = "/clients"
        private const val SERVICE_STATUS_PATH = "/service_status"
        private const val TOAST_PATH = "/toast"
    }
}