/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.util.wear

import android.content.Context
import com.blazecode.tsviewer.data.TsClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

class WearDataManager(val context: Context) {

    private val dataClient by lazy { Wearable.getDataClient(context) }

    companion object {
        private const val CLIENTS_PATH = "/clients"
        private const val CLIENT_LIST_KEY = "clientlist"
        private const val TIME_MILLIS = "timeMillis"
    }

    fun sendClientList(clientList: MutableList<TsClient>) {
        val names = clientList.map { it.nickname }
        var clientArray: Array<String> = arrayOf()
        for (client in names) {
            clientArray += client
        }

        val request = PutDataMapRequest.create(CLIENTS_PATH).apply {
            dataMap.putStringArray(CLIENT_LIST_KEY, clientArray)
            dataMap.putLong(TIME_MILLIS, System.currentTimeMillis())
        }
            .asPutDataRequest()
            .setUrgent()

        val result = dataClient.putDataItem(request)
    }
}