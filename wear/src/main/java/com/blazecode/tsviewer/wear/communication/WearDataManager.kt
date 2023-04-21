/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package com.blazecode.tsviewer.wear.communication

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class WearDataManager(context: Context) {

    private val messageClient by lazy { Wearable.getMessageClient(context) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(context) }

    fun sendStartActivityRequest() {
        send(START_ACTIVITY_PATH)
    }

    private fun send(path: String, data: String = "") {
        GlobalScope.launch {
            try {
                val nodes = capabilityClient
                    .getCapability(MOBILE_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
                    .await()
                    .nodes

                // Send a message to all nodes in parallel
                nodes.map { node ->
                    async {
                        messageClient.sendMessage(node.id, path, data.toByteArray())
                            .await()
                    }
                }.awaitAll()

                Log.i("WearDataManager","Starting activity requests sent successfully")
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Exception) {
                Log.i("WearDataManager","Error sending start activity requests: $exception")
            }
        }
    }

    companion object {
        private const val MOBILE_CAPABILITY = "mobile"
        private const val START_ACTIVITY_PATH = "/start-activity"
    }
}