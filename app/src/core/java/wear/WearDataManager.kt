/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2023.
 *
 */

package wear

import android.content.Context
import com.blazecode.tsviewer.data.ErrorCode
import com.blazecode.tsviewer.data.TsClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import data.WearDataPackage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.lang.reflect.Type

class WearDataManager(context: Context) {

    private val messageClient by lazy { Wearable.getMessageClient(context) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(context) }

    companion object {
        private const val WEAR_CAPABILITY = "wear"
        private const val CLIENTS_PATH = "/clients"
        private const val SERVICE_STATUS_PATH = "/service_status"
        private const val ERROR_CODE_PATH = "/error_code"
        private const val TOAST_PATH = "/toast"
    }

    fun sendClientList(list: MutableList<TsClient>, code: ErrorCode){
        val gson = GsonBuilder().create()
        val gsonType: Type = object : TypeToken<WearDataPackage>() {}.type

        val json = gson.toJson(WearDataPackage(list, System.currentTimeMillis()), gsonType)
        send(CLIENTS_PATH, json)
        send(ERROR_CODE_PATH, code.toString())
        Timber.d("Sent data to wear: $json, $code")
    }

    fun sendToastMessage(message: String) {
        send(TOAST_PATH, message)
    }

    fun sendServiceStatus(isRunning: Boolean) {
        send(SERVICE_STATUS_PATH, isRunning.toString())
        if (!isRunning) send(ERROR_CODE_PATH, ErrorCode.NO_ERROR.toString())
    }

    suspend fun areNodesAvailable(): Boolean{
        try {
            val nodes = capabilityClient
                .getCapability(WEAR_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
                .await()
                .nodes
            return nodes.isNotEmpty()
        } catch (e: ApiException){
            Timber.e("Error checking for nodes: $e")
            return false
        }
    }

    private fun send(path: String, data: String) {
        GlobalScope.launch {
            try {
                val nodes = capabilityClient
                    .getCapability(WEAR_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
                    .await()
                    .nodes

                // Send a message to all nodes in parallel
                nodes.map { node ->
                    async {
                        messageClient.sendMessage(node.id, path, data.toByteArray())
                            .await()
                    }
                }.awaitAll()

                Timber.i("Starting activity requests sent successfully")
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Exception) {
                Timber.i("Error sending start activity requests: $exception")
            }
        }
    }
}