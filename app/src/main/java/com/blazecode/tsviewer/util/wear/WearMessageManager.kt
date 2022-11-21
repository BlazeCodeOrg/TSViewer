/*
 *
 *  * Copyright (c) BlazeCode / Ralf Lehmann, 2022.
 *
 */

package com.blazecode.tsviewer.util.wear

import android.content.Context
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable

class WearMessageManager(val context: Context) {

    private val CLIENT_SHARING_CAPABILITY = "client_sharing"
    private val CLIENT_SHARING_CAPABILITY_PATH = "/client_sharing"
    private var nodeId: String? = null

    fun setupClientSharing(){
        val capabilityInfo: CapabilityInfo = Tasks.await(
            Wearable.getCapabilityClient(context)
                .getCapability(CLIENT_SHARING_CAPABILITY, CapabilityClient.FILTER_REACHABLE)
        )
        updateCapability(capabilityInfo)
    }

    fun sendMessage(clientList: MutableList<String>){
        setupClientSharing()
        val string: String = "test"
        nodeId?.also { nodeId ->
            val sendTask: Task<*> = Wearable.getMessageClient(context).sendMessage(
                nodeId,
                CLIENT_SHARING_CAPABILITY_PATH,
                string.toByteArray(Charsets.UTF_8)
            ).apply {
                addOnSuccessListener { Toast.makeText(context, "Sent", Toast.LENGTH_SHORT).show() }
                addOnFailureListener { Toast.makeText(context, exception.toString(), Toast.LENGTH_SHORT).show() }
            }
        }

    }

    private fun updateCapability(capabilityInfo: CapabilityInfo){
        nodeId = bestNodeId(capabilityInfo.nodes)
    }

    private fun bestNodeId(nodes: Set<Node>): String? {
        return nodes.firstOrNull { it.isNearby }?.id ?: nodes.firstOrNull()?.id
    }
}