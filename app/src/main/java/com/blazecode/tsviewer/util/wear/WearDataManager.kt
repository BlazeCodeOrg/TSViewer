/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

package com.blazecode.tsviewer.util.wear

import android.content.Context
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

class WearDataManager(val context: Context) {

    private val dataClient by lazy { Wearable.getDataClient(context) }

    companion object {
        private const val CLIENTS_PATH = "/clients"
        private const val CLIENT_LIST_KEY = "clientlist"
    }

    fun sendClientList(clientList: MutableList<String>) {
        var clientArray: Array<String> = arrayOf()
        for (client in clientList) {
            clientArray += client
        }

        val request = PutDataMapRequest.create(CLIENTS_PATH).apply {
            dataMap.putStringArray(CLIENT_LIST_KEY, clientArray)
        }
            .asPutDataRequest()
            .setUrgent()

        val result = dataClient.putDataItem(request)
    }
}