/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.wear.communication

import android.util.Log
import android.widget.Toast
import com.blazecode.tsviewer.wear.complication.ComplicationProvider
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.google.gson.GsonBuilder
import data.DataHolder
import data.WearDataPackage
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
                    if(!data.clients.isNullOrEmpty()){
                        DataHolder.list.value = data.clients.toMutableList()
                        ComplicationProvider().update(this@WearableListenerService)
                    } else {
                        DataHolder.list.value = mutableListOf()
                        ComplicationProvider().update(this@WearableListenerService)
                    }
                }

                Log.i("WearableListenerService", "Received ${data.clients.size} clients")
            }
            TEST_PATH -> {
                Toast.makeText(this, "TESTING\n${String(messageEvent.data)}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val CLIENTS_PATH = "/clients"
        private const val TEST_PATH = "/test"
    }
}